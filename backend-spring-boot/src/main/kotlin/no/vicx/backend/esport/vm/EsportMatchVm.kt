package no.vicx.backend.esport.vm

import com.fasterxml.jackson.annotation.JsonProperty

// using nullables due to an unresolved issue with WebFlux
data class EsportMatchVm(
    val id: Long? = null,
    val name: String = "",
    @field:JsonProperty("begin_at")
    val beginAt: String? = null,
    val status: String = "",
)
