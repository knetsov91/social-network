package social.com.notificationservice;

import com.fasterxml.jackson.databind.JsonNode;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {
    private SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "likes-topic", groupId = "likes-group",containerFactory = "myKafkaListenerContainerFactory")
    public void likes(JsonNode node) {
        try {
            Like like = new Like();
            like.setPost(UUID.fromString(node.get("post").asText()));
            like.setUser(UUID.fromString(node.get("user").asText()));
            like.setDate(LocalDateTime.now());

            messagingTemplate.convertAndSend("/topic/likes", like);
        } catch (Exception e) {
            log.error("Failed to process likes event", e);
        }
    }

}
