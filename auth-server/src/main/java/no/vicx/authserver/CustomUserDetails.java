package no.vicx.authserver;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final String name;
    private final String email;
    private final boolean hasImage;

    public CustomUserDetails(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String name,
            String email,
            boolean hasImage) {
        super(username, password, authorities);
        this.name = name;
        this.email = email;
        this.hasImage = hasImage;
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