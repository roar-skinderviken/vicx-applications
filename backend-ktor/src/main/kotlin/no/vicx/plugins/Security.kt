package no.vicx.plugins

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.TimeUnit

fun Application.configureSecurity() {
    val logger = LoggerFactory.getLogger("JWTLogger")

    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtJwks = "$jwtIssuer/oauth2/jwks"

    val jwkProvider: JwkProvider = JwkProviderBuilder(URI(jwtJwks).toURL())
        .cached(10, 24, TimeUnit.HOURS) // Cache up to 10 JWKs for 24 hours
        .build()

    authentication {
        jwt {
            realm = jwtRealm

            verifier(jwkProvider, jwtIssuer) {
                acceptLeeway(30) // Add a 30-second leeway for clock skew
            }

            validate { credential ->
                JWTPrincipal(credential.payload)
//                if (credential.payload.audience.contains(jwtAudience))
//                    JWTPrincipal(credential.payload)
//                else
//                    null
            }

            challenge { _, _ ->
                logger.warn("Some error occurred")
            }
        }
    }
}