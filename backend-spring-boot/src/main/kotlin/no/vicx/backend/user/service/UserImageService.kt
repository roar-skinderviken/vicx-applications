package no.vicx.backend.user.service

import no.vicx.database.user.UserImage
import no.vicx.database.user.UserImageRepository
import no.vicx.database.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

/**
 * Service class for managing user images, including adding, replacing, and deleting images associated with a user.
 *
 * Constructs a `UserImageService` with the required dependencies.
 *
 * @param userService         the service for managing user operations
 * @param userRepository      the repository for user entities
 * @param userImageRepository the repository for user image entities
 */
@Transactional
@Service
class UserImageService(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val userImageRepository: UserImageRepository,
) {
    /**
     * Adds or replaces the image associated with the specified user.
     *
     * @param file     the [MultipartFile] containing the new image
     * @param username the username of the user to update the image for
     */
    fun addOrReplaceUserImage(
        file: MultipartFile,
        username: String,
    ) {
        val user = userService.getUserByUserName(username)

        user.userImage =
            UserImage(
                file.bytes,
                file.contentType,
            )

        userRepository.save(user)
    }

    /**
     * Deletes the image associated with the specified user.
     *
     * @param username the username of the user whose image should be deleted
     */
    fun deleteUserImage(username: String) = userImageRepository.deleteByUserUsername(username)
}
