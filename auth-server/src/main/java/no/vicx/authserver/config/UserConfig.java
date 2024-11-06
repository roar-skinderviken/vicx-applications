package no.vicx.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

    private final UserProperties userProperties;

    public UserConfig(UserProperties userProperties) {
        this.userProperties = userProperties;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername(userProperties.name())
                        .password(userProperties.password())
                        .roles("USER")
                        .build()
        );
    }
}