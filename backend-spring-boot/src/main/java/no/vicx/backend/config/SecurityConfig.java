package no.vicx.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static no.vicx.backend.jwt.JwtUtils.useJwt;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "OPTIONS", "HEAD");
            }
        };
    }

    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/multitenancy.html#oauth2reourceserver-opaqueandjwt
    @Bean
    AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver(
            final JwtDecoder jwtDecoder,
            final OpaqueTokenIntrospector opaqueTokenIntrospector,
            final JwtAuthenticationConverter jwtAuthenticationConverter) {

        var jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        jwtAuthenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
        var jwtProviderManager = new ProviderManager(jwtAuthenticationProvider);

        var opaqueTokenProviderManager = new ProviderManager(
                new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector));

        return request -> useJwt(request)
                ? jwtProviderManager
                : opaqueTokenProviderManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver) throws Exception {
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // port 8082
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()

                        // exposes same info as /actuator/info, but on port 8080
                        .requestMatchers(HttpMethod.GET, "/gitproperties").permitAll()

                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/esport").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                        .requestMatchers("/graphiql", "/graphql").permitAll()

                        .requestMatchers("/messages").hasRole("USER")
                        .requestMatchers("/api/**").hasRole("USER")

                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.authenticationManagerResolver(tokenAuthenticationManagerResolver)
                )
                .build();
    }
}
