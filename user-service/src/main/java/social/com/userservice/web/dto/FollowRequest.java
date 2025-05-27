package social.com.userservice.web.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class FollowRequest {
    private UUID followeId;
}