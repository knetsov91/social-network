package social.com.chatservice.chat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import social.com.chatservice.message.model.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "chats")
public class Chat {

    @Id
    private String id;

    private List<UUID> participants;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @DBRef
    private List<Message> messages;
}