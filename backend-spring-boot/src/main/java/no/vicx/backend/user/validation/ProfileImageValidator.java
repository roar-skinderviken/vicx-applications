package no.vicx.backend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;

import java.util.Base64;
import java.util.List;

public class ProfileImageValidator implements ConstraintValidator<ProfileImage, String> {

    private static final List<String> ALLOWED_FILETYPES = List.of("image/png", "image/jpeg");
    private static final Tika TIKA = new Tika();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    private long maxFileSize;
    private String invalidFileTypeMessage;
    private String invalidSizeMessage;

    @Override
    public void initialize(ProfileImage constraintAnnotation) {
        this.maxFileSize = constraintAnnotation.maxFileSize();
        this.invalidFileTypeMessage = constraintAnnotation.invalidFileTypeMessage();
        this.invalidSizeMessage = constraintAnnotation.invalidSizeMessage();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null or empty check upfront
        if (value == null || value.isEmpty()) {
            return true;
        }

        byte[] decodedBytes;
        try {
            decodedBytes = BASE64_DECODER.decode(value);
        } catch (IllegalArgumentException e) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        // Size check: Ensure the file is within the allowed size limit
        if (decodedBytes.length > maxFileSize) {
            return setValidationError(invalidSizeMessage, context);
        }

        // Validate file type
        if (!ALLOWED_FILETYPES.contains(TIKA.detect(decodedBytes))) {
            return setValidationError(invalidFileTypeMessage, context);
        }

        // If both size and type are valid
        return true;
    }

    private static boolean setValidationError(String message, ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
