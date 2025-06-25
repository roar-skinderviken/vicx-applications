package no.vicx.backend.error

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest

@RestController
class ErrorHandler(
    private val errorAttributes: ErrorAttributes,
) : ErrorController {
    @RequestMapping(ERROR_PATH)
    fun handleError(webRequest: WebRequest): ApiError {
        val attrs =
            errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(
                    ErrorAttributeOptions.Include.STATUS,
                    ErrorAttributeOptions.Include.MESSAGE,
                    ErrorAttributeOptions.Include.PATH,
                ),
            )

        return ApiError(
            status = attrs["status"] as Int,
            message = attrs["message"] as String,
            url = attrs["path"] as String,
        )
    }

    companion object {
        private const val ERROR_PATH = "/error"
    }
}
