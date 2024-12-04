package no.vicx.backend.user.service;

import no.vicx.backend.error.NotFoundException;
import no.vicx.database.user.UserImage;
import no.vicx.database.user.UserImageRepository;
import no.vicx.database.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service class for managing user images, including adding, replacing, and deleting images associated with a user.
 */
@Transactional
@Service
public class UserImageService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;

    /**
     * Constructs a {@code UserImageService} with the required dependencies.
     *
     * @param userService         the service for managing user operations
     * @param userRepository      the repository for user entities
     * @param userImageRepository the repository for user image entities
     */
    public UserImageService(UserService userService, UserRepository userRepository, UserImageRepository userImageRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
    }

    /**
     * Adds or replaces the image associated with the specified user.
     *
     * @param file     the {@link MultipartFile} containing the new image
     * @param username the username of the user to update the image for
     * @throws IOException          if an I/O error occurs while reading the file
     * @throws NullPointerException if {@code file} is {@code null},
     *                              or if {@link MultipartFile#getBytes()} returns {@code null},
     *                              or if {@link MultipartFile#getContentType()} returns {@code null}
     * @throws NotFoundException    if non-existing username
     */
    public void addOrReplaceUserImage(
            final MultipartFile file,
            final String username) throws IOException, NotFoundException {

        var image = new UserImage(
                file.getBytes(),
                file.getContentType());

        var user = userService.getUserByUserName(username);
        user.setUserImage(image);

        userRepository.save(user);
    }

    /**
     * Deletes the image associated with the specified user.
     *
     * @param username the username of the user whose image should be deleted
     * @throws IllegalArgumentException if {@code username} is {@code null}
     */
    public void deleteUserImage(final String username) {
        userImageRepository.deleteByUserUsername(username);
    }
}
