package no.vicx.backend.jacksontests

sealed interface CalculatorOperation {
    data class Add(
        val firstValue: Long,
        val secondValue: Long,
        val something: MySecondLevelType,
    ) : CalculatorOperation

    data class Subtract(
        val firstValue: Long,
        val secondValue: Long,
    ) : CalculatorOperation

    sealed interface MySecondLevelType {
        data class Foo(
            val message: String,
        ) : MySecondLevelType

        data class Bar(
            val errorCount: Long,
        ) : MySecondLevelType
    }
}
