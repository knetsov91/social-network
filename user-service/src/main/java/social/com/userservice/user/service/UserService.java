package social.com.userservice.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import social.com.userservice.user.model.User;
import social.com.userservice.user.repository.UserRepository;
import social.com.userservice.web.dto.UserRegisterRequest;

import java.time.LocalDateTime;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserRegisterRequest userRegisterRequest) {

        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        String encode = passwordEncoder.encode(userRegisterRequest.getPassword());
        User user = new User();
        user.setUsername(userRegisterRequest.getUsername());
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(encode);

        return userRepository.save(user);
    }
}
