package no.vicx.backend.user.service

import no.vicx.backend.user.vm.RecaptchaResponseVm
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder


@Service
class RecaptchaService(
    private val restClient: RestClient,
    @Value("\${recaptcha.secret}") private val recaptchaSecret: String
) {
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
        val uri = baseUriBuilder.cloneBuilder()
            .queryParam(TOKEN_RESPONSE_PARAMETER, token)
            .build().toUri()

        val recaptchaResponseVm = restClient
            .post()
            .uri(uri)
            .retrieve()
            .body(RecaptchaResponseVm::class.java)

        return recaptchaResponseVm?.success ?: false
    }

    private val baseUriBuilder: UriComponentsBuilder =
        UriComponentsBuilder.fromUriString(RECAPTCHA_VERIFY_URL)
            .queryParam(SECRET_REQUEST_PARAMETER, recaptchaSecret)

    companion object {
        const val RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify"
        const val SECRET_REQUEST_PARAMETER = "secret"
        const val TOKEN_RESPONSE_PARAMETER = "response"
    }
}
