package social.com.authservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import social.com.authservice.util.JwtUtil;
import social.com.authservice.web.dto.TokenCreateRequest;
import social.com.authservice.web.dto.TokenCreateResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tokens")
public class JwtController {

    private JwtUtil jwtUtil;

    public JwtController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/issue")
    public ResponseEntity issueToken(@RequestBody TokenCreateRequest token) {
        Map<String, Object> claims = Map.of("userId", token.getUserId(), "username", token.getUsername());
        String jwtToken = jwtUtil.generateToken(claims);
        TokenCreateResponse response = new TokenCreateResponse();
        response.setToken(jwtToken);

        return ResponseEntity.ok().body(response);
    }
}