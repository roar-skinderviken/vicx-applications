package no.vicx.ktor.user.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import no.vicx.ktor.user.vm.RecaptchaResponseVm

class RecaptchaClient(
    private val httpClient: HttpClient,
    private val reCaptchaSecret: String,
) {
    suspend fun verifyToken(token: String): Boolean {
        val url =
            urlBuilder
                .apply {
                    parameters.append(TOKEN_RESPONSE_PARAMETER, token)
                }.build()

        val response = httpClient.post(url)

        return response.status == HttpStatusCode.OK &&
            response.body<RecaptchaResponseVm>().success
    }

    private val urlBuilder =
        URLBuilder(RECAPTCHA_VERIFY_URL).apply {
            parameters.append(SECRET_REQUEST_PARAMETER, reCaptchaSecret)
        }

    companion object {
        const val SECRET_REQUEST_PARAMETER = "secret"
        const val TOKEN_RESPONSE_PARAMETER = "response"
        const val RECAPTCHA_VERIFY_URL: String = "https://www.google.com/recaptcha/api/siteverify"
    }
}
