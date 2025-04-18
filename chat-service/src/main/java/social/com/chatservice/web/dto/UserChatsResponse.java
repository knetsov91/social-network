package social.com.chatservice.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserChatsResponse {

    private UUID chatId;

    private List<UUID> participants;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}