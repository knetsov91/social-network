package social.com.chatservice.web.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageResponse {

    private String id;
    private String text;
    private UUID senderId;
    private UUID receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
