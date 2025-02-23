package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import no.vicx.database.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration(proxyBeanMethods = false)
public class UserDetailsConfig {

    /**
     * Creates a {@link UserDetailsService} bean that retrieves user details based on the username.
     * If the username matches the default user defined in the configuration, the default user is returned.
     * Otherwise, the method attempts to load the user from the provided {@link UserRepository}.
     *
     * @param userProperties  properties of the default user, such as username, password, etc.
     * @param passwordEncoder password encoder used to encode the user's password
     * @param userRepository the repository used to retrieve users from the database
     * @return the {@link UserDetailsService} implementation
     */
    @Bean
    public UserDetailsService userDetailsService(
            DefaultUserProperties userProperties,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        return username -> {
            if (username.equalsIgnoreCase(userProperties.username())) {
                return new CustomUserDetails(
                        userProperties.username(),
                        passwordEncoder.encode(userProperties.password()),
                        userProperties.name(),
                        userProperties.email(),
                        false);
            }

            return userRepository
                    .findByUsername(username)
                    .map(CustomUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        };
    }
}