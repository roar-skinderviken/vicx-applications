package no.vicx.backend.jacksontests

import tools.jackson.databind.DatabindContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.jsontype.PolymorphicTypeValidator

class VicxPolymorphicTypeValidator(
    private val allowedPrefixes: Set<String> = setOf("no.vicx.", "java.", "kotlin."),
) : PolymorphicTypeValidator() {
    override fun validateBaseType(
        ctx: DatabindContext,
        baseType: JavaType,
    ) = validityFor(baseType.rawClass.name)

    override fun validateSubClassName(
        ctx: DatabindContext,
        baseType: JavaType,
        subClassName: String,
    ) = validityFor(subClassName)

    override fun validateSubType(
        ctx: DatabindContext,
        baseType: JavaType,
        subType: JavaType,
    ) = validityFor(subType.rawClass.name)

    private fun validityFor(className: String): Validity =
        if (allowedPrefixes.any { className.startsWith(it) }) Validity.ALLOWED else Validity.DENIED
}
