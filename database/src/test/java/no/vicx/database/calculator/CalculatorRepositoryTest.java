package no.vicx.database.calculator;

import no.vicx.database.PostgresTestContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestContainerConfig.class)
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

        var result = sut.findAllByOrderByIdDesc(PageRequest.of(0, 10));

        var first = result.getContent().getFirst();
        var last = result.getContent().getLast();

        assertTrue(first.getId() > last.getId());
    }

    @Test
    void findAllByOrderByIdDesc_givenThreeItemsInDatabase_expectResultWithTwoElements() {
        entityManager.persist(createValidEntity());
        entityManager.persist(createValidEntity());
        entityManager.persist(createValidEntity());

        var result = sut.findAllByOrderByIdDesc(PageRequest.of(0, 2));

        assertEquals(2, result.getContent().size());
    }

    @Test
    void save_givenValidEntity_expectEntityToBeSaved() {
        var expected = createValidEntity();

        sut.save(expected);

        var actual = entityManager.find(CalcEntry.class, expected.getId());

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
    
    private static CalcEntry createValidEntity(String username) {
        return new CalcEntry(
                1, 2, CalculatorOperation.PLUS, 3, username);
    }

    private static CalcEntry createValidEntity() {
        return createValidEntity(null);
    }
}