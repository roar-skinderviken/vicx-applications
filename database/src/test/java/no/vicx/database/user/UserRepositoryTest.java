package no.vicx.database.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;

import static no.vicx.database.user.RepositoryTestUtils.createUserImage;
import static no.vicx.database.user.RepositoryTestUtils.createValidUser;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
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
    void save_givenValidUser_expectUserInDatabase() {
        var user = createValidUser();

        var savedUser = sut.save(user);

        var userInDb = entityManager.find(VicxUser.class, savedUser.getId());

        assertEquals(savedUser.getId(), userInDb.getId());
        assertEquals(user.getUsername(), userInDb.getUsername());
        assertEquals(user.getPassword(), userInDb.getPassword());
        assertEquals(user.getEmail(), userInDb.getEmail());
    }

    @Test
    void save_givenDuplicateUser_expectException() {
        sut.save(createValidUser());

        var duplicateUser = createValidUser();
        duplicateUser.setUsername("User1");
        assertThrows(DataIntegrityViolationException.class, () -> sut.save(duplicateUser));
    }

    @Test
    void save_givenValidUserWithImage_expectUserWithImageInDatabase() throws IOException {
        var user = createValidUser();
        user.setUserImage(createUserImage());
        var savedUser = sut.save(user);

        var userInDb = entityManager.find(VicxUser.class, savedUser.getId());
        assertEquals(user.getUserImage().getContentType(), userInDb.getUserImage().getContentType());
    }

    @Test
    void save_givenExistingUserWithImageInDb_expectUserWithoutImageAfterSave() throws IOException {
        var user = createValidUser();
        user.setUserImage(createUserImage());
        entityManager.persist(user);
        assertEquals(1, getImageCountInDb());

        var savedUser = sut.findByUsername(user.getUsername())
                .map(userInDb -> {
                    assertNotNull(userInDb.getUserImage());
                    userInDb.setUserImage(null);
                    return sut.save(userInDb);
                }).orElseThrow();

        assertNull(savedUser.getUserImage());
        assertEquals(0, getImageCountInDb());
    }

    @Test
    void findById_givenUserInDatabase_expectUser() {
        var user = createValidUser();
        entityManager.persist(user);

        var userInDb = sut.findById(user.getId());
        assertTrue(userInDb.isPresent());
    }

    @Test
    void findByUsername_givenUserInDatabase_expectUser() {
        var user = createValidUser();
        entityManager.persist(user);

        var userInDb = sut.findByUsername(user.getUsername());
        assertTrue(userInDb.isPresent());

        var upperCaseUser = sut.findByUsername(user.getUsername().toUpperCase());
        assertTrue(upperCaseUser.isPresent());
    }

    Long getImageCountInDb() {
        return (Long) entityManager.getEntityManager()
                .createQuery("SELECT COUNT(1) FROM UserImage")
                .getSingleResult();
    }
}