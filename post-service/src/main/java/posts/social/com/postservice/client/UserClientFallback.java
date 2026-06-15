package posts.social.com.postservice.client;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public List<UUID> getFollowings(UUID userId) {
        return List.of();
    }
}
