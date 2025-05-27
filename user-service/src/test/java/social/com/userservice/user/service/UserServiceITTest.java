package social.com.userservice.user.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import social.com.userservice.auth.service.AuthService;
import social.com.userservice.user.model.User;
import social.com.userservice.user.repository.UserRepository;
import social.com.userservice.web.dto.UserRegisterRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceITTest {

    @Autowired
    private UserService userService;

    @SpyBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthService authService;

    @Test
    void testRegister_saveUserToDatabaseWithEncodedPassword() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("username");
        userRegisterRequest.setPassword("password");
        userRegisterRequest.setConfirmPassword("password");

        Mockito.when(passwordEncoder.encode("password")).thenReturn("password");

        userService.register(userRegisterRequest);

        assertEquals( 1L, userRepository.count());
    }

    @Test
    void testRegister_userNotCreatedWhenUsernameExists() {
        String password = "password";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setUsername("username");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(encodedPassword);

        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("username");
        userRegisterRequest.setPassword(password);
        userRegisterRequest.setConfirmPassword(password);

        userRepository.save(user);

        assertEquals(1L, userRepository.count());

        doReturn(Optional.empty()).when(userRepository).findByUsername("username");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.register(userRegisterRequest);
        });

        assertEquals( 1L, userRepository.count());
    }

}