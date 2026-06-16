package posts.social.com.postservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import posts.social.com.postservice.Like;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEventPollerUTest {

    @InjectMocks
    private OutboxEventPoller outboxEventPoller;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, Like> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void test_poll_whenPendingEventsExist_thenSendsEachToKafkaAndMarksPublished() throws Exception {
        Like like = new Like(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        OutboxEvent event = OutboxEvent.builder()
                .topic("likes-topic")
                .payload("{}")
                .build();

        when(outboxEventRepository.findByPublishedFalse()).thenReturn(List.of(event));
        when(objectMapper.readValue(event.getPayload(), Like.class)).thenReturn(like);

        outboxEventPoller.poll();

        verify(kafkaTemplate).send(eq("likes-topic"), eq(like));
        assertTrue(event.isPublished());
    }

    @Test
    void test_poll_whenNoPendingEvents_thenKafkaIsNeverCalled() throws Exception {
        when(outboxEventRepository.findByPublishedFalse()).thenReturn(List.of());

        outboxEventPoller.poll();

        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void test_poll_whenMultiplePendingEventsExist_thenEachIsSentToKafka() throws Exception {
        Like like1 = new Like(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        Like like2 = new Like(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());

        OutboxEvent event1 = OutboxEvent.builder().topic("likes-topic").payload("{\"event\":\"1\"}").build();
        OutboxEvent event2 = OutboxEvent.builder().topic("likes-topic").payload("{\"event\":\"2\"}").build();

        when(outboxEventRepository.findByPublishedFalse()).thenReturn(List.of(event1, event2));
        when(objectMapper.readValue(event1.getPayload(), Like.class)).thenReturn(like1);
        when(objectMapper.readValue(event2.getPayload(), Like.class)).thenReturn(like2);

        outboxEventPoller.poll();

        verify(kafkaTemplate).send("likes-topic", like1);
        verify(kafkaTemplate).send("likes-topic", like2);
        assertTrue(event1.isPublished());
        assertTrue(event2.isPublished());
    }
}
