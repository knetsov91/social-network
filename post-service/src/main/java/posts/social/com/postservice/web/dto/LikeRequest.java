package posts.social.com.postservice.web.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class LikeRequest {
    private UUID userId;
}
