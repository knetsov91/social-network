package posts.social.com.postservice.web.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class PostCreateRequest {

    private String title;
    private String content;
    private UUID authorId;
}