package no.vicx.backend.user.service

import no.vicx.backend.error.NotFoundException
import no.vicx.backend.user.vm.ChangePasswordVm
import no.vicx.backend.user.vm.CreateUserVm
import no.vicx.backend.user.vm.UserPatchVm
import no.vicx.database.user.UserImage
import no.vicx.database.user.UserRepository
import no.vicx.database.user.VicxUser
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

/**
 * Service for managing user-related operations, such as user creation, updating user details, and password management.
 *
 *
 * This service interacts with the [UserRepository] for persistence operations and the [PasswordEncoder]
 * for encoding passwords.
 * It also handles the creation of user images associated with users and provides functionality for updating
 * and validating user passwords.
 *
 */
@Transactional
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    cacheManager: CacheManager,
) {
    private val recaptchaTokensCache: Cache =
        requireNotNull(
            cacheManager.getCache("RECAPTCHA_TOKENS"),
        ) { "Cache 'RECAPTCHA_TOKENS' is not configured. Application cannot start." }

    /**
     * Creates a new user with the specified details.
     *
     * The user's password is encoded using the provided [PasswordEncoder].
     * If an image file is provided, it will be associated with the user.
     *
     * @param createUserVm the [CreateUserVm] containing the user details
     * @param image        the image file associated with the user (optional)
     * @return the created [VicxUser]
     */
    fun createUser(
        createUserVm: CreateUserVm,
        image: MultipartFile?,
    ): VicxUser {
        val savedUser =
            userRepository.save(
                VicxUser
                    .builder()
                    .username(createUserVm.username)
                    .password(passwordEncoder.encode(createUserVm.password))
                    .name(createUserVm.name)
                    .email(createUserVm.email)
                    .userImage(
                        if (image != null && !image.isEmpty) {
                            UserImage(image.bytes, image.contentType)
                        } else {
                            null
                        },
                    ).build(),
            )

        recaptchaTokensCache.evictIfPresent(createUserVm.recaptchaToken)

        return savedUser
    }

    /**
     * Retrieves a user by their username.
     *
     *
     * If the user does not exist, a [NotFoundException] is thrown.
     *
     *
     * @param username the username of the user to retrieve
     * @return the [VicxUser] corresponding to the specified username
     */
    fun getUserByUserName(username: String): VicxUser =
        userRepository.findByUsername(username).orElseThrow {
            NotFoundException("User $username not found")
        }

    /**
     * Updates the user details with the specified patch information.
     *
     * @param requestVm the [UserPatchVm] containing the details to update
     * @param username  the username of the user to update
     */
    fun updateUser(
        requestVm: UserPatchVm,
        username: String,
    ) {
        val user = getUserByUserName(username)
        userRepository.save(requestVm.applyPatch(user))
    }

    /**
     * Validates if the provided password matches the user's current password.
     *
     * @param username          the username of the user to validate the password for
     * @param clearTextPassword the password in plain text to validate
     * @return `true` if the password matches, `false` otherwise
     */
    fun isValidPassword(
        username: String,
        clearTextPassword: String?,
    ): Boolean =
        passwordEncoder.matches(
            clearTextPassword,
            getUserByUserName(username).password,
        )

    /**
     * Updates the password for the specified user.
     *
     * @param changePasswordVm the [ChangePasswordVm] containing the new password details
     * @param username         the username of the user to update the password for
     */
    fun updatePassword(
        changePasswordVm: ChangePasswordVm,
        username: String,
    ) {
        userRepository.save(
            changePasswordVm.applyPatch(
                getUserByUserName(username),
                passwordEncoder,
            ),
        )
    }
}
