package social.com.chatservice.web.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserChatResponse {

    private String chatId;
    private UUID createdBy;
    private List<String> participantUsernames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
