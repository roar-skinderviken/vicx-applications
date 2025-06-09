package no.vicx.ktor.user.service

import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import no.vicx.ktor.cache.AsyncCacheWrapper
import no.vicx.ktor.db.model.UserImage
import no.vicx.ktor.db.model.VicxUser
import no.vicx.ktor.db.repository.UserRepository
import no.vicx.ktor.user.vm.ChangePasswordVm
import no.vicx.ktor.user.vm.CreateUserVm
import no.vicx.ktor.user.vm.UserPatchVm
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.concurrent.TimeUnit

class UserService(
    recaptchaClient: RecaptchaClient,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
) {
    private val recaptchaCache = AsyncCacheWrapper<String, Boolean>(
        1, TimeUnit.MINUTES,
    ) { recaptchaToken -> recaptchaClient.verifyToken(recaptchaToken) }

    /**
     * Creates a new user with the specified details.
     *
     * The user's password is encoded using the provided [PasswordEncoder].
     * If an image file is provided, it will be associated with the user.
     *
     * @param createUserVm the [CreateUserVm] containing the user details
     * @param userImage the image file associated with the user (optional)
     * @return the created [VicxUser]
     */
    suspend fun createUser(
        createUserVm: CreateUserVm,
        userImage: UserImage?
    ): VicxUser {
        if (!recaptchaCache.getOrCompute(createUserVm.recaptchaToken)) {
            throw RequestValidationException(
                value = createUserVm,
                reasons = listOf("recaptchaToken is invalid. Please wait to token expires and try again")
            )
        }

        if (userRepository.findIdByUsername(createUserVm.username) != null) {
            throw RequestValidationException(
                value = createUserVm,
                reasons = listOf("Username is already in use")
            )
        }

        recaptchaCache.invalidate(createUserVm.recaptchaToken)

        return userRepository.createUser(
            createUserVm.toDbModel(
                encryptedPassword = passwordEncoder.encode(createUserVm.password),
                userImage = userImage
            )
        )
    }

    /**
     * Retrieves a user by their username.
     *
     * If the user with the specified `username` is not found, a [NotFoundException] is thrown.
     *
     * @param username the username of the user to retrieve
     * @return the [VicxUser] corresponding to the specified username
     */
    suspend fun getUserByUserName(username: String): VicxUser =
        userRepository.findByUsername(username)
            ?: throw NotFoundException("User $username not found")

    /**
     * Updates the user details with the specified patch information.
     *
     * If the user with the specified `username` is not found, a [NotFoundException] is thrown.
     *
     * @param requestVm the [UserPatchVm] containing the details to update
     * @param username  the username of the user to update
     */
    suspend fun updateUser(
        requestVm: UserPatchVm,
        username: String
    ) {
        val userToPatch = getUserByUserName(username)
        userRepository.updateUser(
            id = userToPatch.id,
            name = requestVm.name.takeIf { it.isNotBlank() },
            email = requestVm.email.takeIf { it.isNotBlank() },
        )
    }

    /**
     * Validates if the provided password matches the user's current password.
     *
     * If the user with the specified `username` is not found, a [NotFoundException] is thrown.
     *
     * @param username          the username of the user to validate the password for
     * @param clearTextPassword the password in plain text to validate
     * @return `true` if the password matches, `false` otherwise
     */
    fun isValidPassword(
        username: String,
        clearTextPassword: String?
    ): Boolean {
        TODO()
        /*
                return passwordEncoder.matches(
                    clearTextPassword,
                    getUserByUserName(username).getPassword()
                )
        */
    }

    /**
     * Updates the password for the specified user.
     *
     * If the user with the specified `username` is not found, a [NotFoundException] is thrown.
     *
     * @param changePasswordVm the [ChangePasswordVm] containing the new password details
     * @param username         the username of the user to update the password for
     */
    fun updatePassword(
        changePasswordVm: ChangePasswordVm,
        username: String
    ) {
        TODO()
        /*
                userRepository.save<no.vicx.database.user.VicxUser>(
                    changePasswordVm.applyPatch(
                        getUserByUserName(username),
                        passwordEncoder
                    )
                )
        */
    }
}