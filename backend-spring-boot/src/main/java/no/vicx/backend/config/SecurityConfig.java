package no.vicx.backend.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/calculator").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("USER")

                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/image/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/user").hasRole("USER")

                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("USER")

                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new FusedClaimConverter()))
                )
                .headers(headers -> {
                            headers.permissionsPolicyHeader(permissions ->
                                    permissions.policy("geolocation=(), microphone=(), camera=()"));

                            headers.contentSecurityPolicy(policyConfig ->
                                    policyConfig.policyDirectives(
                                            "default-src 'self'; " +
                                                    "script-src 'self'; " +
                                                    "style-src 'self'; " +
                                                    "img-src 'self'; " +
                                                    "font-src 'self'; " +
                                                    "connect-src 'self'; " +
                                                    "media-src 'self'; " +
                                                    "frame-src 'none'; " +  // Example for blocking iframes
                                                    "frame-ancestors 'none'; " +  // Prevent framing
                                                    "form-action 'self'; " +  // Restrict form submissions
                                                    "base-uri 'self';"  // Restrict base URI
                                    )
                            );
                        }
                )
                .build();
    }
}
