package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
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
            DefaultUserProperties userProperties,
            PasswordEncoder passwordEncoder
    ) {
        return new CustomUserDetails(
                userProperties.username(),
                passwordEncoder.encode(userProperties.password()),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                userProperties.name(),
                userProperties.email(),
                false);
    }
}