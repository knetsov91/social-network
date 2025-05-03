package posts.social.com.postservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageablePostsResponse {

    private List<PostResponse> posts;
    private Long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
}