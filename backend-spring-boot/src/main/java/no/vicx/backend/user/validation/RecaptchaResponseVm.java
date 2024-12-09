package no.vicx.backend.user.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

// See https://developers.google.com/recaptcha/docs/verify

public record RecaptchaResponseVm(
        boolean success,
        @JsonProperty("challenge_ts")
        String challengeTimestamp,
        String hostname,
        @JsonProperty("error-codes")
        String[] errorCodes
) {
}
