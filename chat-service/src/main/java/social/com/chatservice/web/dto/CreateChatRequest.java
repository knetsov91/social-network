package social.com.chatservice.web.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CreateChatRequest {

    List<UUID> participants;
}
