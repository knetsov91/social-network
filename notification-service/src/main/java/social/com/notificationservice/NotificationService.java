package social.com.notificationservice;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationService {
    private SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "likes-topic", groupId = "likes-group",containerFactory = "myKafkaListenerContainerFactory")
    public void likes(JsonNode node ) {
        Like like = new Like();
        like.setPost(UUID.fromString(node.get("post").asText()));
        like.setUser(UUID.fromString(node.get("user").asText()));
        like.setDate(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/likes", like);
    }

}
