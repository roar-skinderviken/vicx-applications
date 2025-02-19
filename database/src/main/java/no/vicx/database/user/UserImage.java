package no.vicx.database.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents an image associated with a Vicx user.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserImage {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private VicxUser user;

    @Column(name = "content_type")
    private String contentType;

    // columnDefinition = "BLOB" is required for H2
    @Column(name = "image_data", columnDefinition = "BLOB")
    private byte[] imageData;

    // Constants for null-check messages
    static final String IMAGE_DATA_MUST_NOT_BE_NULL = "Image data must not be null";
    static final String CONTENT_TYPE_MUST_NOT_BE_NULL = "Content type must not be null";

    /**
     * Constructs a new {@code UserImage} instance with the specified values.
     *
     * @param imageData   the binary data of the image; must not be null.
     * @param contentType the MIME type of the image (e.g., "image/png"); must not be null.
     * @throws NullPointerException if {@code imageData} or {@code contentType} is null.
     */
    public UserImage(byte[] imageData, String contentType) {
        this.imageData = Objects.requireNonNull(imageData, IMAGE_DATA_MUST_NOT_BE_NULL);
        this.contentType = Objects.requireNonNull(contentType, CONTENT_TYPE_MUST_NOT_BE_NULL);
    }
}