package no.vicx.backend.user.vm

import com.fasterxml.jackson.annotation.JsonProperty


// See https://developers.google.com/recaptcha/docs/verify
data class RecaptchaResponseVm(
    val success: Boolean = false,
    @JsonProperty("challenge_ts") val challengeTimestamp: String? = null,
    val hostname: String? = null,
    @JsonProperty("error-codes") val errorCodes: List<String>? = null
)
