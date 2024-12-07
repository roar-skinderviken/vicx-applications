package no.vicx.backend.info;

import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gitproperties")
public class GitPropertiesController {

    private final GitProperties gitProperties;

    public GitPropertiesController(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @GetMapping
    public GitProperties getInfo() {
        return gitProperties;
    }
}
