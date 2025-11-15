package no.vicx.backend.user.service

import no.vicx.backend.user.vm.RecaptchaResponseVm
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

@Service
class RecaptchaService(
    restClientBuilder: RestClient.Builder,
    @Value($$"${recaptcha.secret}") private val recaptchaSecret: String,
) {
    private val restClient =
        restClientBuilder
            .baseUrl(RECAPTCHA_VERIFY_BASE_URL)
            .build()

    /**
     * Validates a reCAPTCHA token against the Google reCAPTCHA verification service.
     *
     *
     * This method checks the provided reCAPTCHA token by sending a request to Google's
     * verification API. The result is cached using the token as the cache key to avoid
     * redundant verification calls for the same token.
     *
     *
     * @param token the reCAPTCHA token to validate
     * @return `true` if the token is valid and verified by Google; `false` otherwise
     */
    @Cacheable("RECAPTCHA_TOKENS")
    fun verifyToken(token: String): Boolean {
        val uri =
            UriComponentsBuilder
                .fromPath(SITE_VERIFY_PATH)
                .queryParam(SECRET_REQUEST_PARAMETER, recaptchaSecret)
                .queryParam(TOKEN_RESPONSE_PARAMETER, token)
                .build()
                .toUriString()

        val recaptchaResponseVm =
            restClient
                .post()
                .uri(uri)
                .retrieve()
                .body(RecaptchaResponseVm::class.java)

        return recaptchaResponseVm?.success ?: false
    }

    companion object {
        const val RECAPTCHA_VERIFY_BASE_URL = "https://www.google.com/recaptcha/api"
        const val SITE_VERIFY_PATH = "/siteverify"
        const val SECRET_REQUEST_PARAMETER = "secret"
        const val TOKEN_RESPONSE_PARAMETER = "response"
    }
}
