package no.vicx.authserver;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final String name;
    private final String email;

    public CustomUserDetails(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String name,
            String email) {
        super(username, password, authorities);
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}