package posts.social.com.postservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import posts.social.com.postservice.Like;

import java.util.List;

@Component
public class OutboxEventPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Like> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxEventPoller(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, Like> kafkaTemplate, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void poll() throws Exception {
        List<OutboxEvent> pending = outboxEventRepository.findByPublishedFalse();
        for (OutboxEvent event : pending) {
            Like like = objectMapper.readValue(event.getPayload(), Like.class);
            kafkaTemplate.send(event.getTopic(), like);
            event.setPublished(true);
        }
    }
}
