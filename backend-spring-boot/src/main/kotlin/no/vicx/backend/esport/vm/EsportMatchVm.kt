package no.vicx.backend.esport.vm

import com.fasterxml.jackson.annotation.JsonProperty

data class EsportMatchVm(
    val id: Long,
    val name: String,
    @field:JsonProperty("begin_at")
    val beginAt: String,
    val status: String
)
