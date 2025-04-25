package social.com.chatservice.web.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateMessageRequest {

    private String chatId;

    private UUID senderId;

    private UUID receiverId;

    private String text;
}