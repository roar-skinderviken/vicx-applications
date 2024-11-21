package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DefaultUserConfig {

    @Bean(name = "defaultUserDetails")
    public CustomUserDetails defaultUser(
            @Value("${app-user.name}") String username,
            @Value("${app-user.password}") String password,
            PasswordEncoder passwordEncoder
    ) {
        return new CustomUserDetails(
                username,
                passwordEncoder.encode(password),
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                "John Doe",
                "user@example.com");
    }
}