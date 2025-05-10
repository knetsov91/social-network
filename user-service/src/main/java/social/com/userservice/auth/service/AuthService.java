package social.com.userservice.auth.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import social.com.userservice.auth.client.AuthClient;
import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.auth.client.dto.TokenIssueResponse;
import social.com.userservice.auth.client.dto.TokenValidationRequest;

@Service
public class AuthService {

    private AuthClient authClient;

    public AuthService(AuthClient authClient) {
        this.authClient = authClient;
    }

    public TokenIssueResponse issueToken(TokenIssueRequest request) {
        ResponseEntity<TokenIssueResponse> tokenIssueResponseResponseEntity = authClient.issueToken(request);
        return tokenIssueResponseResponseEntity.getBody();
    }

    public ResponseEntity validateToken(TokenValidationRequest request) {
        ResponseEntity responseEntity = authClient.validateToken(request);
        return responseEntity;
    }
}
