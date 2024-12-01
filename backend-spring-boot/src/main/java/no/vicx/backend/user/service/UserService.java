package no.vicx.backend.user.service;

import no.vicx.backend.error.NotFoundException;
import no.vicx.backend.user.vm.UserPatchRequestVm;
import no.vicx.backend.user.vm.UserVm;
import no.vicx.database.user.UserImage;
import no.vicx.database.user.UserRepository;
import no.vicx.database.user.VicxUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        var user = userVm.toNewVicxUser(passwordEncoder);

        if (image != null && !image.isEmpty()) {
            user.setUserImage(new UserImage(image.getBytes(), image.getContentType()));
        }

        return userRepository.save(user);
    }

    public VicxUser getUserByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User " + username + " not found"));
    }

    public void updateUser(UserPatchRequestVm requestVm, String username) {
        userRepository.save(requestVm.applyPatch(
                getUserByUserName(username),
                passwordEncoder));
    }
}
