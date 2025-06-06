package no.vicx.esport.vm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EsportMatchVm(
    val id: Long,
    val name: String,
    @SerialName("begin_at") // Correctly maps "begin_at" to "beginAt"
    val beginAt: String,
    val status: String
)
