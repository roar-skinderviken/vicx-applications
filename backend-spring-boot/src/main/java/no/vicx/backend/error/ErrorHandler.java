package no.vicx.backend.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
public class ErrorHandler implements ErrorController {

    private static final String ERROR_PATH = "/error";
    private final ErrorAttributes errorAttributes;

    public ErrorHandler(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(ERROR_PATH)
    ApiError handleError(WebRequest webRequest) {
        var attrs = errorAttributes.getErrorAttributes(
                webRequest, ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.STATUS,
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.PATH
                ));

        return new ApiError(
                (int) attrs.get("status"),
                (String) attrs.get("message"),
                (String) attrs.get("path"));
    }
}
