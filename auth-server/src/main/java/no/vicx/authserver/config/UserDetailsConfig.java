package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import no.vicx.database.user.UserRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
public class UserDetailsConfig {

    @Bean
    public UserDetailsService userDetailsService(
            @Value("${default-user.username}") String defaultUsername,
            ObjectFactory<CustomUserDetails> defaultUserFactory,
            UserRepository userRepository) {
        return username -> {
            if (username.equalsIgnoreCase(defaultUsername)) {
                // for localhost testing
                return defaultUserFactory.getObject();
            }

            return userRepository
                    .findByUsername(username)
                    .map(it -> new CustomUserDetails(
                            it.getUsername(),
                            it.getPassword(),
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                            it.getName(),
                            it.getEmail(),
                            it.getUserImage() != null)
                    )
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        };
    }

    @Bean
    @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    public CustomUserDetails defaultUser(
            DefaultUserProperties userProperties,
            PasswordEncoder passwordEncoder) {

        return new CustomUserDetails(
                userProperties.username(),
                passwordEncoder.encode(userProperties.password()),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                userProperties.name(),
                userProperties.email(),
                false);
    }
}