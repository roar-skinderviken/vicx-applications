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

import java.util.stream.IntStream;

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
    void whenEntityIsSaved_expectEntityInDatabase() {
        var expected = createValidEntity();

        sut.save(expected);

        var actual = entityManager.find(CalculatorEntity.class, expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    void givenEntityInDatabase_whenFindById_expectResult() {
        var expected = createValidEntity();

        entityManager.persist(expected);

        var actual = sut.findById(expected.getId());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void givenEntityInDatabaseWithUsername_whenFindById_expectEntityWithUsername() {
        var expected = createValidEntity("user1");

        entityManager.persist(expected);

        var actual = sut.findById(expected.getId());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void givenSingleEntityInDatabase_whenFindByIdNotOrderByIdAsc_expectEmptyResult() {
        var entityToInsert = createValidEntity();
        entityManager.persist(entityToInsert);

        var result = sut.findByIdNotOrderByIdDesc(entityToInsert.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void givenThreeEntitiesInDatabase_whenFindByIdNotOrderByIdAsc_expectResultWithSizeOfTwo() {
        IntStream.rangeClosed(1, 3)
                .mapToObj(it -> createValidEntity())
                .forEach(it -> entityManager.persist(it));

        var result = sut.findByIdNotOrderByIdDesc(3L);

        assertEquals(2, result.size());
        assertEquals(2, result.getFirst().getId());
    }

    private static CalculatorEntity createValidEntity(String username) {
        return new CalculatorEntity(
                1, 2, CalculatorOperation.PLUS, 3, username);
    }

    private static CalculatorEntity createValidEntity() {
        return createValidEntity(null);
    }
}