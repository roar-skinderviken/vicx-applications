package sample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class WebAndRestClientConfig {

    @Bean
    public RestClient restClient(
            RestClient.Builder builder,
            @Value("${messages.backend-base-uri}") String baseUri,
            OAuth2AuthorizedClientManager authorizedClientManager,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        var interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(request -> "messaging-client-oidc");

        OAuth2AuthorizationFailureHandler authorizationFailureHandler =
                OAuth2ClientHttpRequestInterceptor.authorizationFailureHandler(authorizedClientRepository);

        interceptor.setAuthorizationFailureHandler(authorizationFailureHandler);

        return builder
                .baseUrl(baseUri)
                .requestInterceptor(interceptor)
                .build();
    }

    @Bean
    public WebClient webClient(
            WebClient.Builder builder,
            @Value("${messages.backend-base-uri}") String baseUri,
            OAuth2AuthorizedClientManager authorizedClientManager) {

        var oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        return builder
                .baseUrl(baseUri)
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .clientCredentials()
                .build();

        var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientRepository);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }
}
