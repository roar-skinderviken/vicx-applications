package no.vicx.backend.jwt.github;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class GitHubJwtFromOpaqueProducer {
    private final GitHubUserFetcher userFetcher;

    public GitHubJwtFromOpaqueProducer(GitHubUserFetcher userFetcher) {
        this.userFetcher = userFetcher;
    }

    @Cacheable(value = "GITHUB_TOKENS", unless = "#result == null")
    public Jwt createJwt(String token) throws JwtException {
        try {
            var gitHubUserResponse = userFetcher.fetchUser(token);
            return gitHubUserResponse.toJwt();
        } catch (WebClientResponseException.Unauthorized e) {
            throw new JwtException("Invalid or expired GitHub access token", e);
        } catch (Exception e) {
            throw new JwtException("Error validating GitHub token", e);
        }
    }
}