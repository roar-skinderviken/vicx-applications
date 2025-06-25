package no.vicx.backend.info

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.boot.info.GitProperties
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/gitproperties", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "GitProperties", description = "API for getting build information")
class GitPropertiesController(
    private val gitProperties: GitProperties,
) {
    @GetMapping
    fun getInfo(): GitProperties = gitProperties
}
