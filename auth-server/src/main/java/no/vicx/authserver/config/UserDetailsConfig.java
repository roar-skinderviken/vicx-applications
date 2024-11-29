package no.vicx.authserver.config;

import no.vicx.authserver.CustomUserDetails;
import no.vicx.database.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserDetailsConfig {

    private final DefaultUserProperties userProperties;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor to initialize the configuration with required dependencies.
     *
     * @param userProperties  properties of the default user, such as username, password, etc.
     * @param passwordEncoder password encoder used to encode the user's password
     */
    public UserDetailsConfig(
            DefaultUserProperties userProperties,
            PasswordEncoder passwordEncoder) {
        this.userProperties = userProperties;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a {@link UserDetailsService} bean that retrieves user details based on the username.
     * If the username matches the default user defined in the configuration, the default user is returned.
     * Otherwise, the method attempts to load the user from the provided {@link UserRepository}.
     *
     * @param userRepository the repository used to retrieve users from the database
     * @return the {@link UserDetailsService} implementation
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            if (username.equalsIgnoreCase(userProperties.username())) {
                return createDefaultUser();
            }

            return userRepository
                    .findByUsername(username)
                    .map(CustomUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        };
    }

    /**
     * Creates the default {@link CustomUserDetails} instance using the configured user properties and password encoder.
     * This is used when the username matches the default user specified in the configuration.
     *
     * @return a new instance of {@link CustomUserDetails} for the default user
     */
    CustomUserDetails createDefaultUser() {
        return new CustomUserDetails(
                userProperties.username(),
                passwordEncoder.encode(userProperties.password()),
                userProperties.name(),
                userProperties.email(),
                false);
    }
}