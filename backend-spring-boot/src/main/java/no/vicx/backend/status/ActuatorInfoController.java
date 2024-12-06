package no.vicx.backend.status;

import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Profile("!test")
@RestController
@RequestMapping("/info")
public class ActuatorInfoController {

    private final InfoEndpoint infoEndpoint;

    public ActuatorInfoController(InfoEndpoint infoEndpoint) {
        this.infoEndpoint = infoEndpoint;
    }

    @GetMapping
    public Map<String, Object> getInfo() {
        return infoEndpoint.info();
    }
}
