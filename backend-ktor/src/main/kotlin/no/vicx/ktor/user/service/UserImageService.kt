package no.vicx.ktor.user.service

import io.ktor.server.plugins.*
import no.vicx.ktor.db.model.UserImage
import no.vicx.ktor.db.repository.UserImageRepository
import no.vicx.ktor.db.repository.UserRepository

/**
 * Service class for managing user images, including adding, replacing, and deleting images associated with a user.
 */
class UserImageService(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val userImageRepository: UserImageRepository
) {
    /**
     * Adds or replaces the image associated with the specified user.
     *
     * @param userImageModel     the [UserImage] containing the new image
     * @param username the username of the user to update the image for
     */
    suspend fun addOrReplaceUserImage(
        userImageModel: UserImage,
        username: String
    ) {
        val user = userService.getUserByUserName(username)
        val userImageModelWithId = userImageModel.copy(id = user.id)

        if (user.userImage == null) userImageRepository.saveUserImage(userImageModelWithId)
        else userImageRepository.updateUserImage(userImageModelWithId)
    }

    /**
     * Deletes the image associated with the specified user.
     *
     * @param username the username of the user whose image should be deleted
     */
    suspend fun deleteUserImage(username: String) {
        val userId = userRepository.findIdByUsername(username) ?: throw NotFoundException("User $username not found")
        userImageRepository.deleteById(userId)
    }
}