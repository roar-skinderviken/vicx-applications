package no.vicx.backend.calculator;

import no.vicx.backend.calculator.vm.CalcVm;
import no.vicx.backend.calculator.vm.PaginatedCalculations;
import no.vicx.backend.testconfiguration.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Collections;

import static no.vicx.database.calculator.CalculatorOperation.PLUS;
import static org.mockito.Mockito.when;

@GraphQlTest(value = CalculatorGraphQLController.class)
@Import(TestSecurityConfig.class)
class CalculatorGraphQLControllerTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Autowired
    CalculatorGraphQLController calculatorGraphQLController;

    @MockitoBean
    CalculatorService calculatorService;

    @Test
    public void testGetAllCalculations() {
        var calcVmList = Collections.singletonList(
                new CalcVm(
                        1, 1, 2,
                        PLUS, 3, "user1", LocalDateTime.now()));

        when(calculatorService.getAllCalculations(0))
                .thenReturn(new PageImpl<>(calcVmList,
                        PageRequest.of(0, 10),
                        calcVmList.size()));

        var queryString =
                "{ getAllCalculations(page: 0) { calculations { id firstValue secondValue operation result username createdAt } page totalPages } }";

        var expected = new PaginatedCalculations(calcVmList, 0, 1);

        graphQlTester.document(queryString)
                .execute()
                .path("data.getAllCalculations")
                .entity(PaginatedCalculations.class)
                .isEqualTo(expected);
    }
}