package no.vicx.authserver;

import no.vicx.database.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    @Value("${default-user.username}")
    private String defaultUsername;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equalsIgnoreCase(defaultUsername)) {
            // for localhost testing
            LOG.info("Using default user");
            return getDefaultUserDetails();
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
    }

    @Lookup("defaultUserDetails")
    protected CustomUserDetails getDefaultUserDetails() {
        return null;
    }
}
