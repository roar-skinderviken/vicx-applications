package no.vicx.database.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static no.vicx.database.user.RepositoryTestUtils.createUserImage;
import static no.vicx.database.user.RepositoryTestUtils.createValidUser;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(no.vicx.database.PostgresTestContainerConfig.class)
class UserImageRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserImageRepository sut;

    @Test
    void findByUserUsername_givenUserInDatabase_expectUser() throws IOException {
        var user = createValidUser();
        user.setUserImage(createUserImage());

        entityManager.persist(user);

        var userImageInDb = sut.findByUserUsername(user.getUsername());
        assertTrue(userImageInDb.isPresent());
    }

    @Test
    void deleteByUserUsername_givenUserInDatabase_expectImageToBeDeleted() throws IOException {
        var user = createValidUser();
        user.setUserImage(createUserImage());
        entityManager.persist(user);

        sut.deleteByUserUsername(user.getUsername());

        assertEquals(0, getImageCountInDb());
    }

    Long getImageCountInDb() {
        return (Long) entityManager.getEntityManager()
                .createQuery("SELECT COUNT(1) FROM UserImage")
                .getSingleResult();
    }
}