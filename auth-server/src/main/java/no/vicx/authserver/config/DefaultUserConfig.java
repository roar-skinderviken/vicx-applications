package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
public class DefaultUserConfig {

    @Bean(name = "defaultUserDetails")
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CustomUserDetails defaultUser(
            @Value("${app-user.name}") String username,
            @Value("${app-user.password}") String password,
            PasswordEncoder passwordEncoder
    ) {
        return new CustomUserDetails(
                username,
                passwordEncoder.encode(password),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "John Doe",
                "user@example.com",
                false);
    }
}