package no.vicx.backend.esport

import no.vicx.backend.esport.vm.EsportVm
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/esport")
class EsportController(
    private val esportService: EsportService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun matches(): Mono<EsportVm> = esportService.getMatches()
}
