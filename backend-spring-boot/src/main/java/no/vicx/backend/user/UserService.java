package no.vicx.backend.user;

import jakarta.transaction.Transactional;
import no.vicx.backend.error.NotFoundException;
import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.UserImage;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public VicxUser createUser(UserVm userVm, MultipartFile image) throws IOException {
        var user = userVm.toNewVicxUser();

        if (image != null && !image.isEmpty()) {
            UserImage userImage = new UserImage();
            userImage.setContentType(image.getContentType());
            userImage.setImageData(image.getBytes());

            user.setUserImage(userImage);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public VicxUser getUserByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User " + username + " not found"));
    }

    public VicxUser updateUser(UserVm userVm) {
        var userInDb = getUserByUserName(userVm.username());

        userInDb.setPassword(passwordEncoder.encode(userVm.password()));
        userInDb.setName(userVm.name());
        userInDb.setEmail(userVm.email());

        return userRepository.save(userInDb);
    }
}
