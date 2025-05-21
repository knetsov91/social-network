package social.com.userservice.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import social.com.userservice.user.model.User;
import social.com.userservice.user.repository.UserRepository;
import social.com.userservice.web.dto.UserRegisterRequest;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;


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

    @Test
    public void test_happy_case() {
        String password = "password";
        String hashedPassword = "hashedPassword";
        LocalDateTime now = LocalDateTime.now();

        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

        User user = new User();
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername("username");
        user.setActive(true);
        user.setCreatedAt(now);

        when(userRepository.save(user)).thenReturn(user);

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRepository.save(user);

        verify(userRepository, times(1)).save(user);
        Assertions.assertEquals(user.getPassword(), hashedPassword);
        Assertions.assertEquals(user.getUsername(), "username");
        Assertions.assertEquals(user.isActive(), true);
        Assertions.assertEquals(user.getCreatedAt(), now);
    }

}