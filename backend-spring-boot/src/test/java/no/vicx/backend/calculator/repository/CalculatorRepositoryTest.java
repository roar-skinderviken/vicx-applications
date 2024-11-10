package no.vicx.backend.calculator.repository;

import no.vicx.backend.calculator.vm.CalculatorOperation;
import no.vicx.backend.config.RepositoryConfiguration;
import no.vicx.backend.testconfiguration.PostgresTestContainerConfig;
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
@Import({PostgresTestContainerConfig.class, RepositoryConfiguration.class})
class CalculatorRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CalculatorRepository sut;

    @BeforeEach
    void setUp() {
        assertEquals(0, sut.count());
    }

    @Test
    void findAllByOrderByIdDesc_givenTwoItemsInDatabase_expectResultToBeOrderedDescending() {
        entityManager.persist(createValidEntity());
        entityManager.persist(createValidEntity());

        var result = sut.findAllByOrderByIdDesc();

        var first = result.getFirst();
        var last = result.getLast();

        assertTrue(first.getId() > last.getId());
    }

    @Test
    void save_givenValidEntity_expectEntityToBeSaved() {
        var expected = createValidEntity();

        sut.save(expected);

        var actual = entityManager.find(CalculatorEntity.class, expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    void findById_givenEntityInDatabase_expectResult() {
        var expected = createValidEntity();

        entityManager.persist(expected);

        var actual = sut.findById(expected.getId());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findById_givenEntityInDatabaseWithUsername_expectEntityWithUsername() {
        var expected = createValidEntity("user1");

        entityManager.persist(expected);

        var actual = sut.findById(expected.getId());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findAllIdsByUsername_givenDataInDatabase_expectResult() {
        entityManager.persist(createValidEntity("user1"));

        var result = sut.findAllIdsByUsername("user1");

        assertEquals(1, result.size());
    }
    
    private static CalculatorEntity createValidEntity(String username) {
        return new CalculatorEntity(
                1, 2, CalculatorOperation.PLUS, 3, username);
    }

    private static CalculatorEntity createValidEntity() {
        return createValidEntity(null);
    }
}