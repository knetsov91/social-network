package social.com.userservice.auth.client.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TokenIssueRequest {
    private String username;
    private UUID userId;
}
