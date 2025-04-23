package social.com.userservice.web;

import org.springframework.web.bind.annotation.*;
import social.com.userservice.auth.client.AuthClient;
import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.auth.client.dto.TokenIssueResponse;
import social.com.userservice.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/users/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/issue")
    public TokenIssueResponse issueToken(@RequestBody TokenIssueRequest request) {
        TokenIssueResponse tokenIssueResponse = authService.issueToken(request);
        return tokenIssueResponse;
    }
}
