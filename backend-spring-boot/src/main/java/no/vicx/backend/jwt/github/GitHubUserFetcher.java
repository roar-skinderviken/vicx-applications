package no.vicx.backend.jwt.github;

import no.vicx.backend.jwt.github.vm.GitHubUserResponseVm;
import no.vicx.backend.jwt.github.vm.GitHubUserVm;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public record GitHubUserFetcher(
        WebClient webClient,
        GitHubEmailFetcher emailFetcher) {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    static final String USER_URL = "https://api.github.com/user";
    static final String HEADER_SCOPES = "X-OAuth-Scopes";

    public GitHubUserResponseVm fetchUser(String token) {
        var responseEntity = webClient
                .get()
                .uri(USER_URL)
                .header(HEADER_AUTHORIZATION, BEARER_PREFIX + token)
                .retrieve()
                .toEntity(GitHubUserVm.class)
                .block();

        if (responseEntity == null) {
            throw new IllegalStateException("Response entity is null");
        }

        var user = responseEntity.getBody();
        if (user == null || user.isEmpty()) {
            throw new IllegalStateException("User is null");
        }

        var additionalEmailAddress = user.email() == null
            ? emailFetcher.fetchEmail(token)
            : null;

        return new GitHubUserResponseVm(
                responseEntity.getBody(),
                responseEntity.getHeaders().getFirst(HEADER_SCOPES),
                additionalEmailAddress,
                token);
    }
}
