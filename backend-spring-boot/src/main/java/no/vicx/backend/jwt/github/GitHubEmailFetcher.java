package no.vicx.backend.jwt.github;

import no.vicx.backend.jwt.github.vm.GitHubEmailVm;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static no.vicx.backend.jwt.HeaderConstants.BEARER_PREFIX;

@Service
public record GitHubEmailFetcher(WebClient webClient) {
    static final String EMAILS_URL = "https://api.github.com/user/emails";

    public String fetchEmail(String token) {
        return webClient
                .get()
                .uri(EMAILS_URL)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .retrieve()
                .bodyToFlux(GitHubEmailVm.class)
                .filter(GitHubEmailVm::primary)
                .map(GitHubEmailVm::email)
                .blockFirst();
    }
}
