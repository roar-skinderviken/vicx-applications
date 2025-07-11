package no.vicx.authserver.config

import no.vicx.authserver.CustomUserDetails
import no.vicx.database.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration(proxyBeanMethods = false)
class UserDetailsConfig {
    /**
     * Creates a [UserDetailsService] bean that retrieves user details based on the username.
     * If the username matches the default user defined in the configuration, the default user is returned.
     * Otherwise, the method attempts to load the user from the provided [UserRepository].
     *
     * @param userProperties  properties of the default user, such as username, password, etc.
     * @param passwordEncoder password encoder used to encode the user's password
     * @param userRepository the repository used to retrieve users from the database
     * @return the [UserDetailsService] implementation
     */
    @Bean
    fun userDetailsService(
        userProperties: DefaultUserProperties,
        passwordEncoder: PasswordEncoder,
        userRepository: UserRepository,
    ) = UserDetailsService { username ->

        if (username.equals(userProperties.username, ignoreCase = true)) {
            CustomUserDetails(
                userProperties.username,
                passwordEncoder.encode(userProperties.password),
                userProperties.name,
                userProperties.email,
                false,
            )
        } else {
            userRepository
                .findByUsername(username)
                .map(::CustomUserDetails)
                .orElseThrow { UsernameNotFoundException("User not found") }
        }
    }
}
