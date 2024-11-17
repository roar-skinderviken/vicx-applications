package no.vicx.database.user;

import jakarta.persistence.*;

@Entity(name = "user_image")
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

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VicxUser getUser() {
        return user;
    }

    public void setUser(VicxUser user) {
        this.user = user;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}