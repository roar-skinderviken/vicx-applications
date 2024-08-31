package no.javatec.calc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz ->
                authz.anyRequest().permitAll()
        );

        http.headers(headers ->
                headers.contentSecurityPolicy(csp ->
                        csp.policyDirectives(
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
                                        "base-uri 'self';"  // Restrict base URI                        ))
                        )
                )
        );
        return http.build();
    }
}
