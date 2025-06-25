package no.vicx.authserver.config

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UserDetailsService

@SpringBootTest
class UserDetailsConfigIntegrationTest(
    userDetailsService: UserDetailsService,
) : StringSpec({

        "when loading user details a second time, it should not be the same instance as the first one" {
            val firstDefaultUser = userDetailsService.loadUserByUsername("user1")
            val secondDefaultUser = userDetailsService.loadUserByUsername("user1")

            firstDefaultUser shouldNotBeSameInstanceAs secondDefaultUser
        }
    })
