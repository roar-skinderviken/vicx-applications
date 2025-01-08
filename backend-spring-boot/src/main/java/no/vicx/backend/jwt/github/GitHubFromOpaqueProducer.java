package no.vicx.backend.jwt.github;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class GitHubFromOpaqueProducer {
    private final GitHubUserFetcher userFetcher;

    public GitHubFromOpaqueProducer(GitHubUserFetcher userFetcher) {
        this.userFetcher = userFetcher;
    }

    @Cacheable(value = "GITHUB_OPAQUE_PRINCIPALS", unless = "#result == null")
    public OAuth2AuthenticatedPrincipal createPrincipal(String token) throws BadOpaqueTokenException {
        try {
            var gitHubUserResponse = userFetcher.fetchUser(token);
            return gitHubUserResponse.toPrincipal();
        } catch (HttpClientErrorException e) {
            throw new BadOpaqueTokenException("Invalid or expired GitHub access token", e);
        } catch (Exception e) {
            throw new BadOpaqueTokenException("Error validating GitHub token", e);
        }
    }
}