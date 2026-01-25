package no.vicx.backend.jacksontests

sealed class CalculatorOperation {
    abstract val firstValue: Long
    abstract val secondValue: Long

    data class Add(
        override val firstValue: Long,
        override val secondValue: Long,
        val something: MySecondLevelType,
    ) : CalculatorOperation()

    data class Subtract(
        override val firstValue: Long,
        override val secondValue: Long,
    ) : CalculatorOperation()

    sealed interface MySecondLevelType {
        data class Foo(
            val message: String,
        ) : MySecondLevelType

        data class Bar(
            val errorCount: Long,
        ) : MySecondLevelType
    }
}
