package social.com.notificationservice.presence;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PresenceResponse {
    private UUID userId;
    private String status;
}
