package no.vicx.backend.jacksontests

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import tools.jackson.module.kotlin.readValue

class MapperTest :
    BehaviorSpec({

        val mapper =
            JsonMapper
                .builder()
                .polymorphicTypeValidator(VicxPolymorphicTypeValidator())
                .annotationIntrospector(GlobalTypeInfoIntrospector())
                .apply { addModule(KotlinModule.Builder().build()) }
                .build()

        Given("add operation") {
            val add = CalculatorOperation.Add(1, 2, CalculatorOperation.MySecondLevelType.Foo("Hello"))

            When("serializing to JSON") {
                val json = mapper.writeValueAsString(add)

                Then("json should be as expected") {
                    json shouldBe EXPECTED_JSON_FOR_ADD
                }
            }

            When("deserializing from JSON") {
                val deserialized = mapper.readValue<CalculatorOperation>(EXPECTED_JSON_FOR_ADD)

                Then("json should be as expected") {
                    deserialized.shouldBeInstanceOf<CalculatorOperation.Add>()
                    deserialized.something.shouldBeInstanceOf<CalculatorOperation.MySecondLevelType.Foo>()
                }
            }
        }
    }) {
    companion object {
        private const val EXPECTED_JSON_FOR_ADD =
            """{"@class":"no.vicx.backend.jacksontests.CalculatorOperation${'$'}Add","firstValue":1,"secondValue":2,"something":{"@class":"no.vicx.backend.jacksontests.CalculatorOperation${'$'}MySecondLevelType${'$'}Foo","message":"Hello"}}"""
    }
}
