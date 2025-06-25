package no.vicx.backend.calculator

import no.vicx.backend.calculator.vm.CalcVm
import no.vicx.backend.calculator.vm.CalcVm.Companion.fromEntity
import no.vicx.database.calculator.CalcEntry
import no.vicx.database.calculator.CalculatorOperation
import no.vicx.database.calculator.CalculatorRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class CalculatorService(
    private val calculatorRepository: CalculatorRepository,
    @Value("\${app.calculator.max-age}") private val maxAge: Duration,
) {
    fun getAllCalculations(page: Int): Page<CalcVm> {
        val entryPage: Page<CalcEntry> =
            calculatorRepository.findAllBy(
                PageRequest.of(
                    page,
                    10,
                    Sort.by("id").descending(),
                ),
            )
        return entryPage.map(CalcVm::fromEntity)
    }

    fun deleteByIds(ids: List<Long?>) = calculatorRepository.deleteByIdIn(ids)

    fun deleteOldAnonymousCalculations() =
        calculatorRepository.deleteAllByCreatedAtBeforeAndUsernameNull(
            LocalDateTime.now().minus(maxAge),
        )

    fun calculate(
        firstValue: Long,
        secondValue: Long,
        operation: CalculatorOperation,
        username: String?,
    ): CalcVm {
        val result =
            when (operation) {
                CalculatorOperation.PLUS -> firstValue + secondValue
                CalculatorOperation.MINUS -> firstValue - secondValue
            }

        val savedEntity =
            calculatorRepository.save(
                CalcEntry(
                    firstValue,
                    secondValue,
                    operation,
                    result,
                    username,
                ),
            )

        return fromEntity(savedEntity)
    }
}
