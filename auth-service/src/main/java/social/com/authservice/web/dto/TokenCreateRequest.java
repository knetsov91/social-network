package social.com.authservice.web.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TokenCreateRequest {
    private UUID userId;
    private String username;
}