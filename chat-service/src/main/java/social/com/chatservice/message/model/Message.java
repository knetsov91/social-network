package social.com.chatservice.message.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String text;

    private UUID senderId;

    private UUID receiverId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}