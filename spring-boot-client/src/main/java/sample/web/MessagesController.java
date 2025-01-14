package sample.web;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Controller
public class MessagesController {
    private final RestClient restClient;
    private final WebClient webClient;

    public MessagesController(
            RestClient restClient,
            WebClient webClient) {
        this.restClient = restClient;
        this.webClient = webClient;
    }

    // https://spring.io/blog/2024/10/28/restclient-support-for-oauth2-in-spring-security-6-4
    @GetMapping(value = "/messages-restclient-attrs")
    public String messagesUsingRestClientWithAttributes(Model model) {

        String[] messages = restClient
                .get()
                .uri("/messages")
                .retrieve()
                .body(String[].class);

        model.addAttribute("messages", messages);

        return "index";
    }

    @GetMapping(value = "/messages-restclient-header")
    public String messagesUsingRestClientWithAuthHeader(
            Model model,
            @RegisteredOAuth2AuthorizedClient("messaging-client-oidc")
            OAuth2AuthorizedClient authorizedClient) {

        var bearerToken = "Bearer " + authorizedClient.getAccessToken().getTokenValue();

        String[] messages = restClient
                .get()
                .uri("/messages")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .body(String[].class);

        model.addAttribute("messages", messages);

        return "index";
    }

    @GetMapping(value = "/messages-webclient")
    public String messagesUsingWebClient(
            Model model,
            @RegisteredOAuth2AuthorizedClient("messaging-client-oidc")
            OAuth2AuthorizedClient authorizedClient) {

        String[] messages = webClient
                .get()
                .uri("/messages")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String[].class)
                .block();

        model.addAttribute("messages", messages);

        return "index";
    }

    @ExceptionHandler(WebClientResponseException.class)
    public String handleError(Model model, WebClientResponseException ex) {
        model.addAttribute("error", ex.getMessage());
        return "index";
    }
}
