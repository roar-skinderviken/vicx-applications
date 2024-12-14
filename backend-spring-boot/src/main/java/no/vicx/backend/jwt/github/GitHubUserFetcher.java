package no.vicx.backend.jwt.github;

import no.vicx.backend.jwt.github.vm.GitHubUserResponseVm;
import no.vicx.backend.jwt.github.vm.GitHubUserVm;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static no.vicx.backend.jwt.JwtConstants.BEARER_PREFIX;

@Service
public record GitHubUserFetcher(
        RestClient restClient,
        GitHubEmailFetcher emailFetcher) {

    static final String USER_URL = "https://api.github.com/user";
    public static final String HEADER_SCOPES = "X-OAuth-Scopes";

    public GitHubUserResponseVm fetchUser(String token) {
        var responseEntity = restClient
                .get()
                .uri(USER_URL)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .retrieve()
                .toEntity(GitHubUserVm.class);

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
