package no.vicx.backend.calculator.vm

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.vicx.backend.calculator.CalculatorTestUtils.createCalcEntryInTest

class CalcVmTest : StringSpec({

    "given CalcEntry with all fields populated then expect populated CalcVm" {
        val source = createCalcEntryInTest()

        val result = CalcVm.fromEntity(source)

        assertSoftly(result) {
            id shouldBe source.id.toInt()
            firstValue shouldBe source.firstValue.toInt()
            secondValue shouldBe source.secondValue.toInt()
            operation shouldBe source.operation
            username shouldBe source.username
            createdAt shouldBe source.createdAt
        }
    }

    "given CalcEntry without operation then expect exception" {
        val source = createCalcEntryInTest(addOperation = false)

        val thrown = shouldThrow<IllegalStateException> {
            CalcVm.fromEntity(source)
        }

        thrown.message shouldBe "operation cannot be null"
    }

    "given CalcEntry without createdAt then expect exception" {
        val source = createCalcEntryInTest(addCreatedAt = false)

        val thrown = shouldThrow<IllegalStateException> {
            CalcVm.fromEntity(source)
        }

        thrown.message shouldBe "createdAt cannot be null"
    }
})
