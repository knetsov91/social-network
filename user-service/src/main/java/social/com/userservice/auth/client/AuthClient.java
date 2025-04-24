package social.com.userservice.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.auth.client.dto.TokenIssueResponse;

@FeignClient(name="auth-svc", url="${auth.url}")
public interface AuthClient {
    @PostMapping("/issue")
    ResponseEntity<TokenIssueResponse> issueToken(@RequestBody TokenIssueRequest tokenIssueRequest);
}