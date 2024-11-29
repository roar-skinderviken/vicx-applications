package no.vicx.authserver.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertNotSame;

@SpringBootTest
class UserDetailsConfigIntegrationTest {

    @Autowired
    UserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_givenDefaultUsername_expectNewInstanceOnEveryInvocation() {
        var firstDefaultUser = userDetailsService.loadUserByUsername("user1");
        var secondDefaultUser = userDetailsService.loadUserByUsername("user1");

        assertNotSame(firstDefaultUser, secondDefaultUser);
    }
}