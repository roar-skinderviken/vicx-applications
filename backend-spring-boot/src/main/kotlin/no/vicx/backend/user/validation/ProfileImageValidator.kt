package no.vicx.backend.user.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.apache.tika.Tika
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


class ProfileImageValidator : ConstraintValidator<ProfileImage, MultipartFile?> {
    private var maxFileSize: Long = 0
    private var invalidFileTypeMessage: String = ""
    private var invalidSizeMessage: String = ""

    override fun initialize(constraintAnnotation: ProfileImage) {
        this.maxFileSize = constraintAnnotation.maxFileSize
        this.invalidFileTypeMessage = constraintAnnotation.invalidFileTypeMessage
        this.invalidSizeMessage = constraintAnnotation.invalidSizeMessage
    }

    override fun isValid(file: MultipartFile?, context: ConstraintValidatorContext): Boolean {
        // Null or empty check upfront
        if (file == null) return true

        context.disableDefaultConstraintViolation()

        // Size check: Ensure the file is within the allowed size limit
        if (file.size > maxFileSize)
            return setValidationError(invalidSizeMessage, context)

        // Validate file type
        try {
            val mimeType = TIKA.detect(file.bytes)
            if (!ALLOWED_FILETYPES.contains(mimeType))
                return setValidationError(invalidFileTypeMessage, context)
        } catch (e: IOException) {
            // Handle error if file type detection fails
            return setValidationError("Unable to detect file type", context)
        }

        // If both size and type are valid
        return true
    }

    companion object {
        private val TIKA = Tika()

        private val ALLOWED_FILETYPES =
            setOf(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE)

        private fun setValidationError(
            message: String,
            context: ConstraintValidatorContext
        ): Boolean {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation()
            return false
        }
    }
}
