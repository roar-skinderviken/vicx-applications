package no.vicx.ktor.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.Date

object SecurityTestUtils {
    private val keyPair = KeyPairGenerator.getInstance("RSA").apply {
        initialize(2048)
    }.genKeyPair()

    private val privateKey = keyPair.private as RSAPrivateKey
    private val publicKey = keyPair.public as RSAPublicKey
    private val algorithm: Algorithm = Algorithm.RSA256(publicKey, privateKey)

    const val USERNAME_IN_TEST = "user1"

    val tokenStringInTest: String = JWT.create()
        .withIssuer("test-issuer")
//            .withAudience("test-audience")
        .withIssuedAt(Date.from(Instant.now()))
        .withExpiresAt(Date.from(Instant.now().plusSeconds(30)))
        .withHeader(mapOf("alg" to "none"))
        .withClaim("sub", USERNAME_IN_TEST)
        .withClaim("roles", listOf("USER"))
        .sign(algorithm)

    fun Application.configureTestSecurity() {
        install(Authentication) {
            jwt {
                realm = "test-realm"
                verifier {
                    JWT.require(algorithm)
                        .withIssuer("test-issuer")
//                    .withAudience("test-audience")
                        .build()
                }
                validate { credential ->
                    JWTPrincipal(credential.payload) // Accept any payload
                }
            }
        }
    }
}