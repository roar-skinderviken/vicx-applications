package no.vicx.ktor.util

import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import no.vicx.ktor.db.model.UserImage
import org.apache.tika.Tika
import java.io.ByteArrayOutputStream

object MultiPartUtils {
    suspend fun consumeAndValidateUserImageFilePart(imagePart: PartData.FileItem): UserImageResult {
        val fileContent =
            readFileItemAsByteArray(imagePart)
                ?: return UserImageResult.InvalidImageResult(ValidationResult.Invalid(INVALID_FILE_SIZE_MSG))

        val detectedContentType =
            detectContentType(fileContent)
                ?: return UserImageResult.InvalidImageResult(ValidationResult.Invalid(FILE_DETECTION_ERROR_MSG))

        if (!allowedContentTypes.contains(ContentType.parse(detectedContentType))) {
            return UserImageResult.InvalidImageResult(ValidationResult.Invalid(INVALID_CONTENT_TYPE_MSG))
        }

        return UserImageResult.ValidImageResult(
            UserImage(
                contentType = detectedContentType,
                imageData = fileContent,
            ),
        )
    }

    private val tika: Tika = Tika()

    private val allowedContentTypes: Set<ContentType> =
        setOf(
            ContentType.Image.PNG,
            ContentType.Image.JPEG,
        )

    private const val FILE_CHUNK_SIZE = 4_096
    private const val MAX_FILE_SIZE: Long = 50 * 1_024 // Default: 50KB;

    private const val FILE_DETECTION_ERROR_MSG = "Image file: Unable to detect file type"
    private const val INVALID_CONTENT_TYPE_MSG = "Image file type: Only PNG and JPG files are allowed"
    private const val INVALID_FILE_SIZE_MSG =
        "Image file size exceeds the maximum allowed size of $MAX_FILE_SIZE bytes"

    private fun detectContentType(fileContent: ByteArray): String? =
        runCatching {
            tika.detect(fileContent)
        }.getOrNull()

    private suspend fun readFileItemAsByteArray(
        imagePart: PartData.FileItem,
        maxFileSize: Long = MAX_FILE_SIZE,
    ): ByteArray? {
        var fileSize = 0L
        ByteArrayOutputStream().use { outputStream ->
            val byteReadChannel: ByteReadChannel = imagePart.provider.invoke()

            while (!byteReadChannel.isClosedForRead) {
                val buffer = ByteArray(FILE_CHUNK_SIZE)
                val bytesRead = byteReadChannel.readAvailable(buffer)

                fileSize += bytesRead

                if (fileSize > maxFileSize) return null

                outputStream.write(buffer, 0, bytesRead)
            }

            return outputStream.toByteArray()
        }
    }
}
