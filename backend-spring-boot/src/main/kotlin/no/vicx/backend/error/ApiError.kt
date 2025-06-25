package no.vicx.backend.error

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description =
        "Represents a standardized error response structure for API calls, providing " +
            "details about the error, its cause, and related information.",
)
data class ApiError(
    @Schema(
        description = "Timestamp of the error response, representing the time at which the error occurred.",
        example = "1617902400000",
        requiredMode = RequiredMode.REQUIRED,
    )
    val timestamp: Long,
    @Schema(
        description = "HTTP status code associated with the error.",
        example = "404",
        requiredMode = RequiredMode.REQUIRED,
    )
    val status: Int,
    @Schema(
        description = "Message describing the error, typically an explanation of what went wrong.",
        example = "Not Found",
        requiredMode = RequiredMode.REQUIRED,
    )
    val message: String?,
    @Schema(
        description = "The URL that triggered the error, if applicable.",
        example = "/api/user/123",
        requiredMode = RequiredMode.REQUIRED,
    )
    val url: String?,
    @Schema(
        description = "A map of validation errors, where the key is the field name and the value is the error message.",
        example = "{\"username\":\"Username already exists\"}",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    val validationErrors: Map<String, String>? = null,
) {
    constructor(status: Int, message: String, url: String) : this(
        System.currentTimeMillis(),
        status,
        message,
        url,
    )

    constructor(
        status: Int,
        message: String?,
        url: String?,
        validationErrors: Map<String, String>?,
    ) : this(System.currentTimeMillis(), status, message, url, validationErrors)
}
