package social.com.authservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import social.com.authservice.token.service.TokenService;
import social.com.authservice.web.dto.InvalidateTokenRequest;
import social.com.authservice.token.repository.TokenRepository;
import social.com.authservice.util.JwtUtil;
import social.com.authservice.web.dto.TokenCreateRequest;
import social.com.authservice.web.dto.TokenCreateResponse;
import social.com.authservice.web.dto.TokenValidateRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tokens")
public class JwtController {

    private JwtUtil jwtUtil;
    private TokenRepository tokenRepository;
    private TokenService tokenService;

    public JwtController(JwtUtil jwtUtil, TokenRepository tokenRepository, TokenService tokenService) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/issue")
    public ResponseEntity issueToken(@RequestBody TokenCreateRequest token) {
        Map<String, Object> claims = Map.of("userId", token.getUserId(), "username", token.getUsername());
        String jwtToken = jwtUtil.generateToken(claims);
        TokenCreateResponse response = new TokenCreateResponse();
        response.setToken(jwtToken);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/validate")
    public ResponseEntity validateToken(@RequestBody TokenValidateRequest token) {
       tokenService.isValid(token);
        jwtUtil.isTokenValid(token.token());

        return ResponseEntity.ok().body(token);
    }

    @PostMapping("/invalidate")
    public ResponseEntity blacklistToken(@RequestBody InvalidateTokenRequest token) {
        tokenService.invalidateToken(token);
        return ResponseEntity.ok().build();
    }
}