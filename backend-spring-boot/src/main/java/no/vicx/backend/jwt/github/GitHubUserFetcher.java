package no.vicx.backend.jwt.github;

import no.vicx.backend.jwt.github.vm.GitHubUserResponseVm;
import no.vicx.backend.jwt.github.vm.GitHubUserVm;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static no.vicx.backend.jwt.JwtUtils.BEARER_PREFIX;

@Service
public record GitHubUserFetcher(RestClient restClient) {

    static final String USER_URL = "https://api.github.com/user";
    public static final String SCOPES_HEADER = "X-OAuth-Scopes";

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

        return new GitHubUserResponseVm(
                user,
                responseEntity.getHeaders().getFirst(SCOPES_HEADER),
                token);
    }
}
