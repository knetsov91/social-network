package social.com.userservice.user.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import social.com.userservice.user.repository.UserRepository;
import social.com.userservice.web.dto.UserRegisterRequest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserService.class)
class UserServiceITTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;
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
}