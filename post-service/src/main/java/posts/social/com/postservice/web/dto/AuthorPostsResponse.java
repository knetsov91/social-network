package posts.social.com.postservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorPostsResponse {
    private String title;
    private String content;
    private UUID authorId;
    private List<UUID> likes;
}