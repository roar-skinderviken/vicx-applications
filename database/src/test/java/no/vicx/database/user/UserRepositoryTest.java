package no.vicx.database.user;

import no.vicx.database.RepositoryTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;

import static no.vicx.database.user.RepositoryTestUtils.*;
import static no.vicx.database.user.VicxUser.VALID_BCRYPT_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends RepositoryTestBase {

    @Autowired
    UserRepository sut;

    @BeforeEach
    void setUp() {
        assertEquals(0, sut.count());
    }

    @Test
    void save_givenValidUser_expectUserInDatabase() {
        var savedUser = sut.save(createValidUser());

        entityManager.clear();
        var userInDb = entityManager.find(VicxUser.class, savedUser.getId());

        assertEquals(savedUser.getId(), userInDb.getId());
        assertEquals(savedUser.getUsername(), userInDb.getUsername());
        assertEquals(VALID_BCRYPT_PASSWORD, userInDb.getPassword());
        assertEquals(savedUser.getName(), userInDb.getName());
        assertEquals(savedUser.getEmail(), userInDb.getEmail());
    }

    @Test
    void save_givenDuplicateUser_expectDataIntegrityViolationException() {
        sut.save(createValidUser());

        var duplicateUser = createValidUser();
        duplicateUser.setUsername("User1");
        assertThrows(DataIntegrityViolationException.class, () -> sut.save(duplicateUser));
    }

    @Test
    void save_givenValidUserWithImage_expectUserWithImageInDatabase() throws IOException {
        var savedUser = sut.save(createValidUser(createPngUserImage()));

        var userInDb = entityManager.find(VicxUser.class, savedUser.getId());
        var imageInDb = entityManager.find(UserImage.class, savedUser.getId());

        assertEquals(userInDb.getUserImage().getId(), imageInDb.getId());
        assertEquals(userInDb.getUserImage().getContentType(), imageInDb.getContentType());
    }

    @Test
    void save_givenUserWithImageInDb_expectUserWithoutImageAfterSave() throws IOException {
        var user = createValidUser(createPngUserImage());
        entityManager.persist(user);
        assertEquals(1, getImageCountInDb());

        var savedUser = sut.findByUsername(user.getUsername()).orElseThrow();
        assertNotNull(savedUser.getUserImage());

        savedUser.setUserImage(null);
        sut.save(savedUser);
        assertEquals(0, getImageCountInDb());
    }

    @Test
    void save_givenUserWithImage_expectUserWithUpdatedImage() throws IOException {
        var user = createValidUser(createPngUserImage());
        assertEquals(IMAGE_PNG, user.getUserImage().getContentType());

        entityManager.persist(user);
        assertEquals(1, getImageCountInDb());

        var savedUser = sut.findByUsername(user.getUsername()).orElseThrow();
        assertNotNull(savedUser.getUserImage());

        // set new image and save
        savedUser.setUserImage(createJpegUserImage());
        sut.save(savedUser);
        assertEquals(1, getImageCountInDb());

        var imageInDb = entityManager.find(UserImage.class, savedUser.getId());
        assertEquals(IMAGE_JPEG, imageInDb.getContentType());
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

    long getImageCountInDb() {
        return (long) entityManager
                .createQuery("SELECT COUNT(1) FROM UserImage")
                .getSingleResult();
    }
}