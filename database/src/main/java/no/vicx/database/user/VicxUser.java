package no.vicx.database.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Represents a user in the Vicx database.
 */
@Data
@NoArgsConstructor
@Entity
public class VicxUser {
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

    /**
     * Constructs a new {@code VicxUser} instance with the specified values.
     *
     * @param username  the unique username of the user; must not be null.
     * @param password  the user's password; must not be null.
     * @param name      the full name of the user; must not be null.
     * @param email     the user's email address; must not be null.
     * @param userImage the associated user image, or null if not applicable.
     * @throws NullPointerException if {@code username}, {@code password}, {@code name}, or {@code email} is null.
     */
    public VicxUser(String username, String password, String name, String email, UserImage userImage) {
        this.username = Objects.requireNonNull(username, USERNAME_MUST_NOT_BE_NULL);
        this.password = Objects.requireNonNull(password, PASSWORD_MUST_NOT_BE_NULL);
        this.name = Objects.requireNonNull(name, NAME_MUST_NOT_BE_NULL);
        this.email = Objects.requireNonNull(email, EMAIL_MUST_NOT_BE_NULL);
        setUserImage(userImage);
    }

    public void setUserImage(UserImage userImage) {
        if (userImage != null) {
            userImage.setUser(this);
        }
        this.userImage = userImage;
    }
}
