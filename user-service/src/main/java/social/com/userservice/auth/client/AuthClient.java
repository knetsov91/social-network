package social.com.userservice.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.auth.client.dto.TokenIssueResponse;
import social.com.userservice.auth.client.dto.TokenValidationRequest;

@FeignClient(name = "auth-service")
public interface AuthClient {
    @PostMapping("/api/v1/tokens/issue")
    ResponseEntity<TokenIssueResponse> issueToken(@RequestBody TokenIssueRequest tokenIssueRequest);

    @PostMapping("/api/v1/tokens/validate")
    ResponseEntity validateToken(@RequestBody TokenValidationRequest tokenValidationRequest);

    @PostMapping("/api/v1/tokens/is-invalidated")
    ResponseEntity isInvalidated(@RequestBody TokenValidationRequest token);
}