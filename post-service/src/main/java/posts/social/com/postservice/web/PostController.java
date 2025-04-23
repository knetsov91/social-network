package posts.social.com.postservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import posts.social.com.postservice.post.service.PostService;
import posts.social.com.postservice.web.dto.PostCreateRequest;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity post(@RequestHeader(name="Authorization") String authorization, @RequestBody PostCreateRequest postCreateRequest) {
        if (!authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        postService.create(postCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
