package social.com.notificationservice.presence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresenceServiceUTest {

    @InjectMocks
    private PresenceService presenceService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void test_markOnline_whenCalled_thenSetsRedisKeyAndBroadcastsOnlineStatus() {
        UUID userId = UUID.randomUUID();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        presenceService.markOnline(userId);

        verify(valueOperations).set("presence:" + userId, "ONLINE", Duration.ofSeconds(60));
        verify(messagingTemplate).convertAndSend("/topic/presence", new PresenceResponse(userId, "ONLINE"));
    }

    @Test
    void test_markOffline_whenUserIdProvided_thenDeletesRedisKeyAndBroadcastsOfflineStatus() {
        UUID userId = UUID.randomUUID();

        presenceService.markOffline(userId);

        verify(redisTemplate).delete("presence:" + userId);
        verify(messagingTemplate).convertAndSend("/topic/presence", new PresenceResponse(userId, "OFFLINE"));
    }
}
