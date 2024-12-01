package no.vicx.database.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Represents a user in the Vicx database.
 */
@Data
@NoArgsConstructor
@Entity
public class VicxUser {

    // for tests
    public static final String VALID_PLAINTEXT_PASSWORD = "Your-passw0rd";
    public static final String VALID_BCRYPT_PASSWORD = "$2a$10$crhj38nvydnz7y5z/cvrzobamfyichhrachgvgsb0cop46awnaad6";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private UserImage userImage;

    // Constants for null-check messages
    static final String USERNAME_MUST_NOT_BE_NULL = "Username must not be null";
    static final String PASSWORD_MUST_NOT_BE_NULL = "Password must not be null";
    static final String NAME_MUST_NOT_BE_NULL = "Name must not be null";
    static final String EMAIL_MUST_NOT_BE_NULL = "Email must not be null";

    // Password check constant
    static final String PASSWORD_MUST_BE_ENCRYPTED = "Password must be encrypted";

    /**
     * Constructs a new {@code VicxUser} instance with the specified values.
     *
     * @param username  the unique username of the user; must not be null.
     * @param password  the user's password; must not be null.
     * @param name      the full name of the user; must not be null.
     * @param email     the user's email address; must not be null.
     * @param userImage the associated user image, or null if not applicable.
     * @throws NullPointerException if {@code username}, {@code password}, {@code name}, or {@code email} is null.
     * @throws IllegalArgumentException if {@code password} is not encrypted.
     */
    public VicxUser(
            final String username, final String password, final String name,
            final String email, final UserImage userImage) {
        this.username = requireNonNull(username, USERNAME_MUST_NOT_BE_NULL);
        this.password = requireNonNullAndBCrypt(password).toLowerCase();
        this.name = requireNonNull(name, NAME_MUST_NOT_BE_NULL);
        this.email = requireNonNull(email, EMAIL_MUST_NOT_BE_NULL);

        setUserImage(userImage);
    }

    public void setPassword(final String password) {
        this.password = requireNonNullAndBCrypt(password);
    }

    public void setUserImage(final UserImage userImage) {
        if (userImage != null) {
            userImage.setUser(this);
        }
        this.userImage = userImage;
    }

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[ayb]?\\$\\d{2}\\$.{53}$");

    private static String requireNonNullAndBCrypt(final String password) {
        requireNonNull(password, PASSWORD_MUST_NOT_BE_NULL);

        if (!BCRYPT_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(PASSWORD_MUST_BE_ENCRYPTED);
        }

        return password;
    }
}
