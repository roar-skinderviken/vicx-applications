package no.vicx.backend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ProfileImageValidator implements ConstraintValidator<ProfileImage, MultipartFile> {

    private static final List<String> ALLOWED_FILETYPES = List.of("image/png", "image/jpeg");
    private static final Tika TIKA = new Tika();

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
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // Null or empty check upfront
        if (file == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        // Size check: Ensure the file is within the allowed size limit
        if (file.getSize() > maxFileSize) {
            return setValidationError(invalidSizeMessage, context);
        }

        // Validate file type
        try {
            String mimeType = TIKA.detect(file.getBytes());
            if (!ALLOWED_FILETYPES.contains(mimeType)) {
                return setValidationError(invalidFileTypeMessage, context);
            }
        } catch (IOException e) {
            // Handle error if file type detection fails
            return setValidationError("Unable to detect file type", context);
        }

        // If both size and type are valid
        return true;
    }

    private static boolean setValidationError(String message, ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
