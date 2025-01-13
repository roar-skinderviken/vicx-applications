package sample.web;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Controller
public class MessagesController {
    private final WebClient webClient;

    public MessagesController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping(value = "/messages")
    public String authorizationCodeGrant(
            Model model,
            @RegisteredOAuth2AuthorizedClient("messaging-client-oidc")
            OAuth2AuthorizedClient authorizedClient) {

        String[] messages = this.webClient
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
