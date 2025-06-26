package no.vicx.ktor.user.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import no.vicx.ktor.user.vm.RecaptchaResponseVm

class RecaptchaClient(
    private val httpClient: HttpClient,
    private val reCaptchaSecret: String,
) {
    suspend fun verifyToken(token: String): Boolean =
        httpClient
            .post(buildUrl(token))
            .run {
                status == HttpStatusCode.OK &&
                    body<RecaptchaResponseVm>().success
            }

    private fun buildUrl(token: String): Url =
        URLBuilder(RECAPTCHA_VERIFY_URL)
            .apply {
                parameters.append(TOKEN_RESPONSE_PARAMETER, token)
                parameters.append(SECRET_REQUEST_PARAMETER, reCaptchaSecret)
            }.build()

    companion object {
        const val RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify"
        const val SECRET_REQUEST_PARAMETER = "secret"
        const val TOKEN_RESPONSE_PARAMETER = "response"
    }
}
