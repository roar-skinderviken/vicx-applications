package no.vicx.backend.user.vm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecaptchaResponseVm(
        boolean success,
        @JsonProperty("challenge_ts")
        String challengeTimestamp,
        String hostname,
        @JsonProperty("error-codes")
        String[] errorCodes
) {
}
