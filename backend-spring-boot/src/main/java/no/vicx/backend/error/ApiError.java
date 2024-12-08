package no.vicx.backend.error;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Represents a standardized error response structure for API calls, providing " +
        "details about the error, its cause, and related information.")
public record ApiError(

        @Schema(
                description = "Timestamp of the error response, representing the time at which the error occurred.",
                example = "1617902400000",
                requiredMode = REQUIRED)
        long timestamp,

        @Schema(
                description = "HTTP status code associated with the error.",
                example = "404",
                requiredMode = REQUIRED)
        int status,

        @Schema(
                description = "Message describing the error, typically an explanation of what went wrong.",
                example = "Not Found",
                requiredMode = REQUIRED)
        String message,

        @Schema(
                description = "The URL that triggered the error, if applicable.",
                example = "/api/user/123",
                requiredMode = REQUIRED)
        String url,

        @Schema(
                description = "A map of validation errors, where the key is the field name and the value is the error message.",
                example = "{\"username\":\"Username already exists\"}",
                requiredMode = NOT_REQUIRED)
        Map<String, String> validationErrors) {

    public ApiError(int status, String message, String url) {
        this(System.currentTimeMillis(), status, message, url, null);
    }

    public ApiError(int status, String message, String url, Map<String, String> validationErrors) {
        this(System.currentTimeMillis(), status, message, url, validationErrors);
    }
}
