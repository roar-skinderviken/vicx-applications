package no.vicx.backend.jwt.github;

import no.vicx.backend.jwt.github.vm.GitHubEmailVm;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static no.vicx.backend.jwt.JwtConstants.BEARER_PREFIX;

@Service
public record GitHubEmailFetcher(RestClient restClient) {
    static final String EMAILS_URL = "https://api.github.com/user/emails";

    public String fetchEmail(String token) {
        var emailVms = restClient
                .get()
                .uri(EMAILS_URL)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubEmailVm>>() {
                });

        if (emailVms == null) {
            return null;
        }

        return emailVms.stream()
                .filter(GitHubEmailVm::primary)
                .map(GitHubEmailVm::email)
                .findFirst()
                .orElse(null);
    }
}
