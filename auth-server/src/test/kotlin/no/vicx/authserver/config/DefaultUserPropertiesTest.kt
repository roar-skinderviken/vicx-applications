package no.vicx.authserver.config

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row4
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.springframework.beans.factory.getBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class DefaultUserPropertiesTest :
    BehaviorSpec({
        Given("context with all properties") {
            val contextRunner =
                sharedContextRunner
                    .withUserConfiguration(DefaultUserPropertiesTestConfig::class.java)
                    .withPropertyValues(
                        "default-user.username=~username~",
                        "default-user.password=~password~",
                        "default-user.name=~name~",
                        "default-user.email=~email~",
                    )

            Then("expect context to load and properties to be populated") {
                contextRunner.run { context ->
                    assertThat(context).hasSingleBean(DefaultUserProperties::class.java)
                    val defaultUserProperties = context.getBean<DefaultUserProperties>()

                    assertSoftly(defaultUserProperties) {
                        username shouldBe "~username~"
                        password shouldBe "~password~"
                        name shouldBe "~name~"
                        email shouldBe "~email~"
                    }
                }
            }
        }

        Given("context with invalid properties") {
            forAll(
                Row4(" ", "~password~", "~name~", "~email~"),
                Row4("~username~", " ", "~name~", "~email~"),
                Row4("~username~", "~password~", " ", "~email~"),
                Row4("~username~", "~password~", "~name~", " "),
            ) { username, password, name, email ->

                val contextRunner =
                    sharedContextRunner
                        .withUserConfiguration(DefaultUserPropertiesTestConfig::class.java)
                        .withPropertyValues(
                            "default-user.username=$username",
                            "default-user.password=$password",
                            "default-user.name=$name",
                            "default-user.email=$email",
                        )

                Then("expect context to fail loading, $username $password $name $email") {
                    contextRunner.run { context -> assertThat(context).hasFailed() }
                }
            }
        }
    }) {
    @EnableConfigurationProperties(DefaultUserProperties::class)
    class DefaultUserPropertiesTestConfig

    companion object {
        private val sharedContextRunner = ApplicationContextRunner()
    }
}
