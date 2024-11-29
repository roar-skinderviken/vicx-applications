package no.vicx.authserver;

import no.vicx.database.user.VicxUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

public class CustomUserDetails extends User {
    public static final Set<GrantedAuthority> GRANTED_AUTHORITIES = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    private final String name;
    private final String email;
    private final boolean hasImage;

    /**
     * Default constructor for creating a CustomUserDetails instance.
     *
     * @param username username
     * @param password encrypted password
     * @param name     user's real name
     * @param email    user's email address
     * @param hasImage <code>true</code> if user has a profile image
     */
    public CustomUserDetails(
            String username,
            String password,
            String name,
            String email,
            boolean hasImage) {
        super(username, password, GRANTED_AUTHORITIES);
        this.name = name;
        this.email = email;
        this.hasImage = hasImage;
    }

    /**
     * Convenient constructor for creating a CustomUserDetails instance from a VicxUser.
     *
     * @param user VicxUser from the database
     */
    public CustomUserDetails(VicxUser user) {
        super(user.getUsername(), user.getPassword(), GRANTED_AUTHORITIES);
        this.name = user.getName();
        this.email = user.getEmail();
        this.hasImage = user.getUserImage() != null;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean hasImage() {
        return hasImage;
    }
}