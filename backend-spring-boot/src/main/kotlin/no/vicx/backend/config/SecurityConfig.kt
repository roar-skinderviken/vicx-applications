package no.vicx.backend.config

import jakarta.servlet.http.HttpServletRequest
import no.vicx.backend.jwt.JwtUtils
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurer(): WebMvcConfigurer =
        object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry
                    .addMapping("/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "OPTIONS", "HEAD")
            }
        }

    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/multitenancy.html#oauth2reourceserver-opaqueandjwt
    @Bean
    fun tokenAuthenticationManagerResolver(
        jwtDecoder: JwtDecoder,
        opaqueTokenIntrospector: OpaqueTokenIntrospector,
        jwtAuthenticationConverter: JwtAuthenticationConverter,
    ): AuthenticationManagerResolver<HttpServletRequest> {
        val jwtProviderManager =
            ProviderManager(
                JwtAuthenticationProvider(jwtDecoder).apply {
                    setJwtAuthenticationConverter(jwtAuthenticationConverter)
                },
            )

        val opaqueTokenProviderManager =
            ProviderManager(
                OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector),
            )

        return AuthenticationManagerResolver { request ->
            if (JwtUtils.detectJwtToken(request)) {
                jwtProviderManager
            } else {
                opaqueTokenProviderManager
            }
        }
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        tokenAuthenticationManagerResolver: AuthenticationManagerResolver<HttpServletRequest>,
    ): SecurityFilterChain =
        http
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { csrfConfigurer -> csrfConfigurer.disable() }
            .cors {}
            .formLogin { formLoginConfigurer -> formLoginConfigurer.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(EndpointRequest.to(HealthEndpoint::class.java))
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/gitproperties")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/esport")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/user")
                    .permitAll()
                    .requestMatchers("/graphiql", "/graphql")
                    .permitAll()
                    .requestMatchers("/messages")
                    .hasRole("USER")
                    .requestMatchers("/api/**")
                    .hasRole("USER")
                    .requestMatchers("/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2.authenticationManagerResolver(tokenAuthenticationManagerResolver)
            }.build()
}
