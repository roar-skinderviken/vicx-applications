package no.vicx.backend.user.service;

import no.vicx.backend.error.NotFoundException;
import no.vicx.backend.user.vm.ChangePasswordVm;
import no.vicx.backend.user.vm.CreateUserVm;
import no.vicx.backend.user.vm.UserPatchVm;
import no.vicx.database.user.UserImage;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service for managing user-related operations, such as user creation, updating user details, and password management.
 * <p>
 * This service interacts with the {@link UserRepository} for persistence operations and the {@link PasswordEncoder}
 * for encoding passwords.
 * It also handles the creation of user images associated with users and provides functionality for updating
 * and validating user passwords.
 * </p>
 */
@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Cache recaptchaTokensCache;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.recaptchaTokensCache = cacheManager.getCache("RECAPTCHA_TOKENS");

        if (this.recaptchaTokensCache == null) {
            throw new IllegalStateException("Cache 'RECAPTCHA_TOKENS' is not configured. Application cannot start.");
        }
    }

    /**
     * Creates a new user with the specified details.
     * <p>
     * The user's password is encoded using the provided {@link PasswordEncoder}.
     * If an image file is provided, it will be associated with the user.
     * </p>
     *
     * @param createUserVm the {@link CreateUserVm} containing the user details
     * @param image        the image file associated with the user (optional)
     * @return the created {@link VicxUser}
     * @throws IOException if an I/O error occurs while handling the image file
     */
    public VicxUser createUser(
            final CreateUserVm createUserVm,
            final MultipartFile image) throws IOException {
        var savedUser = userRepository.save(VicxUser.builder()
                .username(createUserVm.username())
                .password(passwordEncoder.encode(createUserVm.password()))
                .name(createUserVm.name())
                .email(createUserVm.email())
                .userImage(image != null && !image.isEmpty()
                        ? new UserImage(image.getBytes(), image.getContentType())
                        : null)
                .build());

        recaptchaTokensCache.evictIfPresent(createUserVm.recaptchaToken());

        return savedUser;
    }

    /**
     * Retrieves a user by their username.
     * <p>
     * If the user does not exist, a {@link NotFoundException} is thrown.
     * </p>
     *
     * @param username the username of the user to retrieve
     * @return the {@link VicxUser} corresponding to the specified username
     * @throws NotFoundException if the user with the specified {@code username} is not found
     */
    public VicxUser getUserByUserName(final String username) throws NotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User " + username + " not found"));
    }

    /**
     * Updates the user details with the specified patch information.
     *
     * @param requestVm the {@link UserPatchVm} containing the details to update
     * @param username  the username of the user to update
     * @throws NotFoundException if the user with the specified {@code username} is not found
     */
    public void updateUser(
            final UserPatchVm requestVm,
            final String username) throws NotFoundException {
        userRepository.save(requestVm.applyPatch(getUserByUserName(username)));
    }

    /**
     * Validates if the provided password matches the user's current password.
     *
     * @param username          the username of the user to validate the password for
     * @param clearTextPassword the password in plain text to validate
     * @return {@code true} if the password matches, {@code false} otherwise
     * @throws NotFoundException if the user with the specified {@code username} is not found
     */
    public boolean isValidPassword(
            final String username,
            final String clearTextPassword) throws NotFoundException {
        return passwordEncoder.matches(
                clearTextPassword,
                getUserByUserName(username).getPassword());
    }

    /**
     * Updates the password for the specified user.
     *
     * @param changePasswordVm the {@link ChangePasswordVm} containing the new password details
     * @param username         the username of the user to update the password for
     * @throws NotFoundException if the user with the specified {@code username} is not found
     */
    public void updatePassword(
            final ChangePasswordVm changePasswordVm,
            final String username) throws NotFoundException {
        userRepository.save(changePasswordVm.applyPatch(
                getUserByUserName(username),
                passwordEncoder));
    }
}
