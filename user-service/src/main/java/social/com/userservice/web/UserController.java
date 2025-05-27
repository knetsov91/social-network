package social.com.userservice.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.auth.client.dto.TokenIssueResponse;
import social.com.userservice.auth.client.dto.TokenValidationRequest;
import social.com.userservice.auth.service.AuthService;
import social.com.userservice.user.model.User;
import social.com.userservice.user.service.UserService;
import social.com.userservice.web.dto.ErrorResponse;
import social.com.userservice.web.dto.GetAllUsersResponse;
import social.com.userservice.web.dto.UserLoginRequest;
import social.com.userservice.web.dto.UserRegisterRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private AuthService authService;
    public UserController(UserService userService, AuthenticationManager authenticationManager, AuthService authService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {

        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getConfirmPassword())) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Passwords don't match");
            errorResponse.setCode("400");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        User user = userService.register(userRegisterRequest);

        return ResponseEntity.ok(userRegisterRequest);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {

        Authentication auht = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(auht);
        User user = (User) auht.getPrincipal();

        TokenIssueRequest tokenIssueRequest = Mapper.mapUserToTokenIssueRequest(user);
        TokenIssueResponse tokenIssueResponse = authService.issueToken(tokenIssueRequest);
        ResponseCookie responseCookie = ResponseCookie.from("token", tokenIssueResponse.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3600000)
                .sameSite(SameSiteCookies.NONE.toString())
                .build();
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<GetAllUsersResponse>> getAllUsers() {
        List<User> users = userService.getAll();
        List<GetAllUsersResponse> getAllUsersResponses = Mapper.mapUsersToGetAllUsersResponse(users);
        return ResponseEntity.ok(getAllUsersResponses);
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<?> isAuthenticated(HttpServletRequest req) {
        Optional<Cookie> token = Arrays.stream(req.getCookies()).filter(c -> c.getName().equals("token")).findFirst();

        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        TokenValidationRequest tokenValidationRequest = new TokenValidationRequest(token.get().getValue());

        authService.validateToken(tokenValidationRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}