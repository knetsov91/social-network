package posts.social.com.postservice;

import java.time.LocalDateTime;
import java.util.UUID;

public record Like(UUID postId, UUID userId, LocalDateTime timestamp) {}
