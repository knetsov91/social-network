package social.com.userservice.auth.service;

import feign.FeignException;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import social.com.userservice.auth.client.AuthClient;
import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.auth.client.dto.TokenIssueResponse;
import social.com.userservice.auth.client.dto.TokenValidationRequest;
import social.com.userservice.exceptions.ExpiredTokenException;


@Service
public class AuthService {

    Logger logger = LoggerFactory.getLogger(AuthService.class);
    private AuthClient authClient;

    public AuthService(AuthClient authClient) {
        this.authClient = authClient;
    }

    public TokenIssueResponse issueToken(TokenIssueRequest request) {
        ResponseEntity<TokenIssueResponse> tokenIssueResponseResponseEntity = authClient.issueToken(request);
        return tokenIssueResponseResponseEntity.getBody();
    }

    public ResponseEntity validateToken(TokenValidationRequest request) {
        ResponseEntity responseEntity = null;
        try {
            responseEntity = authClient.validateToken(request);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            throw  new ExpiredTokenException(e.getMessage(), e);
        }
        return responseEntity;
    }
}