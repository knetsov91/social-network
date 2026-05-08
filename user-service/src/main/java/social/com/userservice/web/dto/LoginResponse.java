package social.com.userservice.web.dto;

import java.util.UUID;

public record LoginResponse(UUID id, String username) {
}
