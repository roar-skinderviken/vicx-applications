package no.vicx.backend.stackoverflow.valueserializermodifier;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.module.SimpleModule;

@TestConfiguration
public class JsonMapperConfig {

    @Bean
    public JsonMapperBuilderCustomizer baseModelCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("BaseModelModule");
            module.setSerializerModifier(new MySerializerModifier());
            builder.addModule(module);
        };
    }
}
