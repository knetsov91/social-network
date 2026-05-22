package social.com.notificationservice.presence;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class PresenceService {

    private static final String PRESENCE_KEY_PREFIX = "presence:";
    private static final Duration TTL = Duration.ofSeconds(60);

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public PresenceService(StringRedisTemplate redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    public void markOnline(UUID userId) {
        redisTemplate.opsForValue().set(PRESENCE_KEY_PREFIX + userId, "ONLINE", TTL);
        messagingTemplate.convertAndSend("/topic/presence", new PresenceResponse(userId, "ONLINE"));
    }

    public void markOffline(UUID userId) {
        redisTemplate.delete(PRESENCE_KEY_PREFIX + userId);
        messagingTemplate.convertAndSend("/topic/presence", new PresenceResponse(userId, "OFFLINE"));
    }

    public boolean isOnline(UUID userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PRESENCE_KEY_PREFIX + userId));
    }
}
