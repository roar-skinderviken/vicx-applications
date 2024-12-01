package no.vicx.database.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static no.vicx.database.user.RepositoryTestUtils.createPngUserImage;
import static no.vicx.database.user.RepositoryTestUtils.createValidUser;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@Import(no.vicx.database.PostgresTestContainerConfig.class)
class UserImageRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserImageRepository sut;

    @Test
    void findByUserUsername_givenUserInDatabase_expectUser() throws IOException {
        var imageInTest = createPngUserImage();
        var userInTest = createValidUser(imageInTest);

        entityManager.persist(userInTest);

        var optionalImageInDb = sut.findByUserUsername(userInTest.getUsername());

        assertTrue(optionalImageInDb.isPresent());
        var imageInDb = optionalImageInDb.get();

        assertNotNull(imageInDb.getUser());
        assertNotNull(imageInDb.getId());
        assertEquals(imageInTest.getImageData(), imageInDb.getImageData());
        assertEquals(imageInTest.getContentType(), imageInDb.getContentType());
    }

    @Test
    void deleteByUserUsername_givenUserInDatabase_expectImageToBeDeleted() throws IOException {
        var user = createValidUser(createPngUserImage());
        entityManager.persist(user);

        sut.deleteByUserUsername(user.getUsername());

        assertEquals(0, getImageCountInDb());
    }

    long getImageCountInDb() {
        return (long) entityManager.getEntityManager()
                .createQuery("SELECT COUNT(1) FROM UserImage")
                .getSingleResult();
    }
}