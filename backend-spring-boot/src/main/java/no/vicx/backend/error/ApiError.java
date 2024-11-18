package no.vicx.backend.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        long timestamp,
        int status,
        String message,
        String url,
        Map<String, String> validationErrors) {

    public ApiError(int status, String message, String url) {
        this(System.currentTimeMillis(), status, message, url, null);
    }

    public ApiError(int status, String message, String url, Map<String, String> validationErrors) {
        this(System.currentTimeMillis(), status, message, url, validationErrors);
    }
}
