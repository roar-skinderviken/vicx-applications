package no.vicx.authserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration(proxyBeanMethods = false)
class SecurityConfig {
    // required for Swagger on localhost
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration =
            CorsConfiguration().apply {
                allowedOrigins = listOf("http://localhost:8080")
                allowedMethods = listOf("POST")
                allowedHeaders = listOf("Access-Control-Allow-Origin", "x-requested-with")
            }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    // https://docs.spring.io/spring-authorization-server/reference/getting-started.html#defining-required-components
    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val authorizationServerConfigurer =
            OAuth2AuthorizationServerConfigurer.authorizationServer()

        http
            .cors {} // required for Swagger on localhost
            .securityMatcher(authorizationServerConfigurer.endpointsMatcher)
            .with(
                authorizationServerConfigurer,
            ) { authorizationServer ->
                authorizationServer.oidc {}
            }.authorizeHttpRequests { authorize ->
                authorize.anyRequest().authenticated()
            }
            // Redirect to the login page when not authenticated from the authorization endpoint
            .exceptionHandling { exceptions ->
                exceptions
                    .defaultAuthenticationEntryPointFor(
                        LoginUrlAuthenticationEntryPoint("/login"),
                        MediaTypeRequestMatcher(MediaType.TEXT_HTML),
                    )
            }

        return http.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(
        http: HttpSecurity,
        @Value("\${oauth.post-logout-redirect-uri}") postLogoutRedirectUri: String?,
    ): SecurityFilterChain {
        http
            .cors {} // required for Swagger on localhost
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(EndpointRequest.to(HealthEndpoint::class.java))
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.logout { logout -> logout.logoutSuccessUrl(postLogoutRedirectUri) }
            .formLogin {}

        return http.build()
    }
}
