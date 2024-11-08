package no.vicx.database.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(no.vicx.database.PostgresTestContainerConfig.class)
class UserRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository sut;

    @BeforeEach
    void setUp() {
        assertEquals(0, sut.count());
    }

    @Test
    void whenUserIsSaved_thenUserExistsInDatabase() {
        var user = createValidUser();

        var savedUser = sut.save(user);

        var userInDb = entityManager.find(VicxUser.class, savedUser.getId());

        assertEquals(savedUser.getId(), userInDb.getId());
        assertEquals(user.getUsername(), userInDb.getUsername());
        assertEquals(user.getPassword(), userInDb.getPassword());
        assertEquals(user.getEmail(), userInDb.getEmail());
        assertEquals(user.getImage(), userInDb.getImage());
    }

    @Test
    void givenUserInDatabase_whenFindById_expectUser() {
        var user = createValidUser();
        entityManager.persist(user);

        var userInDb = sut.findById(user.getId());

        assertTrue(userInDb.isPresent());
    }

    @Test
    void givenUserInDatabase_whenFindByUsername_expectUser() {
        var user = createValidUser();
        entityManager.persist(user);

        var userInDb = sut.findByUsername(user.getUsername());

        assertTrue(userInDb.isPresent());
    }

    static VicxUser createValidUser() {
        var user = new VicxUser();
        user.setUsername("user1");
        user.setName("Foo Bar");
        user.setEmail("user1@vicx.no");
        user.setPassword("password1");
        user.setImage("some base64-encoded image");
        return user;
    }
}