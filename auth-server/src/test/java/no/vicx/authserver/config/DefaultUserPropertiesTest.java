package no.vicx.authserver.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultUserPropertiesTest {

    private static final ApplicationContextRunner CONTEXT_RUNNER = new ApplicationContextRunner();

    @EnableConfigurationProperties(DefaultUserProperties.class)
    static class DefaultUserPropertiesTestConfig {
    }

    @Test
    void contextLoad_givenValidDefaultUserProperties_expectContextToLoad() {
        CONTEXT_RUNNER
                .withUserConfiguration(DefaultUserPropertiesTestConfig.class)
                .withPropertyValues(
                        "default-user.username=~username~",
                        "default-user.password=~password~",
                        "default-user.name=~name~",
                        "default-user.email=~email~"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultUserProperties.class);

                    var sut = context.getBean(DefaultUserProperties.class);

                    assertEquals("~username~", sut.username());
                    assertEquals("~password~", sut.password());
                    assertEquals("~name~", sut.name());
                    assertEquals("~email~", sut.email());
                });
    }

    @ParameterizedTest
    @MethodSource("invalidArgumentsTestSource")
    void contextLoad_givenInvalidDefaultUserProperties_expectContextLoadToFail(
            String username, String password, String name, String email) {

        CONTEXT_RUNNER
                .withUserConfiguration(DefaultUserPropertiesTestConfig.class)
                .withPropertyValues(
                        "default-user.username=" + username,
                        "default-user.password=" + password,
                        "default-user.name=" + name,
                        "default-user.email=" + email
                )
                .run(context -> assertThat(context).hasFailed());
    }

    static Stream<Arguments> invalidArgumentsTestSource() {
        return Stream.of(
                Arguments.of(" ", "~password~", "~name~", "~email~"),
                Arguments.of("~username~", " ", "~name~", "~email~"),
                Arguments.of("~username~", "~password~", " ", "~email~"),
                Arguments.of("~username~", "~password~", "~name~", " ")
        );
    }
}