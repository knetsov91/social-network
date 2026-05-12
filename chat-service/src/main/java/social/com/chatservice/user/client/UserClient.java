package social.com.chatservice.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import social.com.chatservice.user.client.dto.UserResponse;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable UUID id);
}
