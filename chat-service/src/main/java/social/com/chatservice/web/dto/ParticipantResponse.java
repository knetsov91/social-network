package social.com.chatservice.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ParticipantResponse {

    private UUID id;
    private String username;
}
