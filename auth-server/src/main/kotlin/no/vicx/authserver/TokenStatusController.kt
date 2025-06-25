package no.vicx.authserver

import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenStatusController(
    private val authorizationService: OAuth2AuthorizationService,
) {
    @GetMapping("token-status")
    fun tokenStatus(
        @RequestParam token: String,
    ): ResponseEntity<*> {
        val authorization =
            authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN)

        if (authorization == null) {
            log.warn("Non-existent token: {}", token)
            return ResponseEntity.notFound().build<Any>()
        }

        val accessToken = authorization.accessToken

        return if (accessToken.isInvalidated) {
            log.info("Token isInvalidated: {}", token)
            ResponseEntity.ok("Token isInvalidated")
        } else {
            log.info("Token not invalidated: {}", token)
            ResponseEntity.ok("Token not invalidated")
        }
    }

    companion object {
        private val log = loggerFor<TokenStatusController>()
    }
}
