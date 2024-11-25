package no.vicx.backend.jwt.github;

import no.vicx.backend.jwt.github.vm.GitHubEmailVm;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public record GitHubEmailFetcher(WebClient webClient) {
    static final String EMAILS_URL = "https://api.github.com/user/emails";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public String fetchEmail(String token) {
        return webClient
                .get()
                .uri(EMAILS_URL)
                .header(HEADER_AUTHORIZATION, BEARER_PREFIX + token)
                .retrieve()
                .bodyToFlux(GitHubEmailVm.class)
                .filter(GitHubEmailVm::primary)
                .map(GitHubEmailVm::email)
                .blockFirst();
    }
}
