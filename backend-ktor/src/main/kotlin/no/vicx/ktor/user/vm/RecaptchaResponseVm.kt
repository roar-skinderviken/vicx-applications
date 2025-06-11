package no.vicx.ktor.user.vm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecaptchaResponseVm(
    val success: Boolean = false,
    @SerialName("challenge_ts") val challengeTimestamp: String = "",
    val hostname: String = "",
    @Suppress("ArrayInDataClass") @SerialName("error-codes") val errorCodes: Array<String> = emptyArray(),
)
