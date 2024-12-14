package no.vicx.authserver;

import no.vicx.database.user.VicxUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Objects;
import java.util.Set;

public class CustomUserDetails extends User {
    public static final Set<GrantedAuthority> GRANTED_AUTHORITIES = Set.of(new SimpleGrantedAuthority("USER"));

    public static final String NAME_NOT_NULL_MSG = "Name cannot be null";
    public static final String EMAIL_NOT_NULL_MSG = "Email cannot be null";

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

        this.name = Objects.requireNonNull(name, NAME_NOT_NULL_MSG);
        this.email = Objects.requireNonNull(email, EMAIL_NOT_NULL_MSG);
        this.hasImage = hasImage;
    }

    /**
     * Convenient constructor for creating a CustomUserDetails instance from a VicxUser.
     *
     * @param user VicxUser from the database
     */
    public CustomUserDetails(VicxUser user) {
        this(user.getUsername(), user.getPassword(), user.getName(),
                user.getEmail(), user.getUserImage() != null);
    }

    @IgnoreCoverage
    public String getName() {
        return this.name;
    }

    @IgnoreCoverage
    public String getEmail() {
        return this.email;
    }

    @IgnoreCoverage
    public boolean hasImage() {
        return this.hasImage;
    }
}