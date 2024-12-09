package no.vicx.backend.user.service;

import no.vicx.backend.user.validation.RecaptchaResponseVm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RecaptchaService {
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private final WebClient webClient;

    public RecaptchaService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Validates a reCAPTCHA token against the Google reCAPTCHA verification service.
     * <p>
     * This method checks the provided reCAPTCHA token by sending a request to Google's
     * verification API. The result is cached using the token as the cache key to avoid
     * redundant verification calls for the same token.
     * </p>
     *
     * @param token the reCAPTCHA token to validate
     * @return {@code true} if the token is valid and verified by Google; {@code false} otherwise
     */
    @SuppressWarnings("unused")
    @Cacheable("RECAPTCHA_TOKENS")
    public boolean verifyToken(String token) {
        var url = String.format("%s?secret=%s&response=%s", RECAPTCHA_VERIFY_URL, recaptchaSecret, token);

        var responseMono = webClient
                .post()
                .uri(url)
                .retrieve()
                .bodyToMono(RecaptchaResponseVm.class);

        var response = responseMono.block();
        return response != null && response.success();
    }
}
