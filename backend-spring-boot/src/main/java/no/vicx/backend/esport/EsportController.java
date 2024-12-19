package no.vicx.backend.esport;

import no.vicx.backend.esport.vm.EsportVm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/esport")
public class EsportController {

    private final EsportService esportService;

    public EsportController(EsportService esportService) {
        this.esportService = esportService;
    }

    @GetMapping
    public EsportVm getMatches() {
        return esportService.getMatches();
    }
}
