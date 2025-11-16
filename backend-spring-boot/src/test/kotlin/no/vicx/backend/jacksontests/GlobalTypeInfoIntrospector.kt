package no.vicx.backend.jacksontests

import com.fasterxml.jackson.annotation.JsonTypeInfo
import tools.jackson.core.Version
import tools.jackson.databind.AnnotationIntrospector
import tools.jackson.databind.cfg.MapperConfig
import tools.jackson.databind.introspect.Annotated
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder

class GlobalTypeInfoIntrospector : AnnotationIntrospector() {
    override fun findTypeResolverBuilder(
        config: MapperConfig<*>,
        annotated: Annotated,
    ): StdTypeResolverBuilder =
        StdTypeResolverBuilder().init(
            JsonTypeInfo.Value.construct(
                JsonTypeInfo.Id.CLASS,
                JsonTypeInfo.As.PROPERTY,
                "@class",
                null,
                true,
                true,
            ),
            null,
        )

    override fun version(): Version = Version(3, 0, 0, null, "tools.jackson", "jackson-databind")
}
