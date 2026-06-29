package social.com.notificationservice.presence;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceEventListener {

    private final PresenceService presenceService;
    private final Map<String, UUID> sessionUserMap = new ConcurrentHashMap<>();

    public PresenceEventListener(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userIdHeader = accessor.getFirstNativeHeader("userId");
        if (userIdHeader == null) return;

        UUID userId = UUID.fromString(userIdHeader);
        sessionUserMap.put(accessor.getSessionId(), userId);
        presenceService.markOnline(userId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        UUID userId = sessionUserMap.remove(event.getSessionId());
        if (userId == null) return;

        presenceService.markOffline(userId);
    }
}
