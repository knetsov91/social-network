package social.com.userservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import social.com.userservice.user.model.User;
import social.com.userservice.user.service.UserService;
import social.com.userservice.web.dto.UserRegisterRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterRequest> register(@RequestBody UserRegisterRequest userRegisterRequest) {

        User user = userService.register(userRegisterRequest);

        return ResponseEntity.ok(userRegisterRequest);
    }
}