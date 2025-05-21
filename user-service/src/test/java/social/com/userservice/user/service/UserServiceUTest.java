package social.com.userservice.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import social.com.userservice.user.repository.UserRepository;
import social.com.userservice.web.dto.UserRegisterRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceUTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void test_whenPasswordAndConfirmPasswordDontMatch_thenThrowException() {

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setPassword("password");
        userRegisterRequest.setConfirmPassword("password1");

        Assertions.assertThrows(RuntimeException.class, () -> userService.register(userRegisterRequest));

    }

}