package no.vicx.authserver.config

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.Row4
import io.kotest.data.forAll
import io.mockk.*
import no.vicx.authserver.UserTestUtils.customUserDetailsInTest
import no.vicx.authserver.config.JwtCustomizerConfig.Companion.EMAIL_CLAIM
import no.vicx.authserver.config.JwtCustomizerConfig.Companion.IMAGE_CLAIM
import no.vicx.authserver.config.JwtCustomizerConfig.Companion.NAME_CLAIM
import no.vicx.authserver.config.JwtCustomizerConfig.Companion.ROLES_CLAIM
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext

class JwtCustomizerConfigTest : BehaviorSpec({

    Given("a JwtCustomizerConfig") {
        val jwtEncodingContext: JwtEncodingContext = mockk()
        val authentication: Authentication = mockk()
        val claimsBuilder: JwtClaimsSet.Builder = mockk(relaxed = true)

        val sut = JwtCustomizerConfig().jwtCustomizer()

        beforeContainer {
            clearAllMocks()

            every { jwtEncodingContext.getPrincipal<Authentication>() } returns authentication
            every { jwtEncodingContext.tokenType } returns OAuth2TokenType(OidcParameterNames.ID_TOKEN)
            every { jwtEncodingContext.claims } returns claimsBuilder
            every { authentication.authorities } returns listOf(SimpleGrantedAuthority("USER"))
            every { authentication.principal } returns customUserDetailsInTest()
        }

        When("calling customize without authentication") {
            every { jwtEncodingContext.getPrincipal<Authentication>() } returns null

            sut.customize(jwtEncodingContext)

            Then("expect no calls to claims") {
                verify(exactly = 0) { jwtEncodingContext.claims }
            }
        }

        When("calling customize with access token") {
            every { jwtEncodingContext.tokenType } returns OAuth2TokenType.ACCESS_TOKEN

            sut.customize(jwtEncodingContext)

            Then("expect only call to claim with roles") {
                verifySequence { claimsBuilder.claim(ROLES_CLAIM, listOf("USER")) }
            }
        }

        When("calling customize, context with ID token, but principal is not CustomUserDetails") {
            every { authentication.principal } returns User(
                "~username~",
                "~password~",
                setOf(SimpleGrantedAuthority("USER"))
            )

            sut.customize(jwtEncodingContext)

            Then("expect only call to claim with roles") {
                verifySequence { claimsBuilder.claim(ROLES_CLAIM, listOf("USER")) }
            }
        }

        forAll(
            Row4(
                "No scopes",
                emptySet(), false, emptySet()
            ),
            Row4(
                "With email scope",
                setOf(OidcScopes.EMAIL), false, setOf(EMAIL_CLAIM to "~email~")
            ),
            Row4(
                "With profile scope",
                setOf(OidcScopes.PROFILE), false, setOf(NAME_CLAIM to "~name~")
            ),
            Row4(
                "With profile scope and user has image",
                setOf(OidcScopes.PROFILE), true, setOf(NAME_CLAIM to "~name~", IMAGE_CLAIM to "~username~")
            ),
            Row4(
                "With both email and profile scopes and user has image",
                setOf(
                    OidcScopes.EMAIL,
                    OidcScopes.PROFILE
                ), true,
                setOf(
                    EMAIL_CLAIM to "~email~",
                    NAME_CLAIM to "~name~",
                    IMAGE_CLAIM to "~username~"
                )
            )
        ) { description, scopes, hasImage, expectedClaims ->

            When("calling customize: $description") {
                every { authentication.authorities } returns listOf(SimpleGrantedAuthority("USER"))
                every { jwtEncodingContext.authorizedScopes } returns scopes
                every { authentication.principal } returns customUserDetailsInTest(hasImage)

                sut.customize(jwtEncodingContext)

                Then("expect calls to claim") {
                    val allExpected = setOf(ROLES_CLAIM to listOf("USER")) + expectedClaims

                    verifySequence {
                        allExpected.forEach { claim ->
                            claimsBuilder.claim(claim.first, claim.second)
                        }
                    }
                }
            }
        }
    }
})