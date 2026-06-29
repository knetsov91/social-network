package social.com.chatservice.web.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ChatResponse {
    private String id;
    private UUID createdBy;
    private List<UUID> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MessageResponse> messages;
}
