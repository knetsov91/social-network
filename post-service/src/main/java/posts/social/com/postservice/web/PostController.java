package posts.social.com.postservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.post.service.PostService;
import posts.social.com.postservice.web.dto.AuthorPostsResponse;
import posts.social.com.postservice.web.dto.LikeRequest;
import posts.social.com.postservice.web.dto.PostCreateRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity post(@RequestBody PostCreateRequest postCreateRequest) {
        postService.create(postCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AuthorPostsResponse>> getUserPosts(@PathVariable UUID userId) {
        List<Post> posts = postService.getPosts(userId);
        return ResponseEntity.ok(Mapper.mapListPostsToListAuthorPostsResponse(posts));
    }

    @PutMapping("/{postId}/likes")
    public ResponseEntity like(@PathVariable UUID postId, @RequestBody LikeRequest likeRequest) {
        boolean liked = postService.togglePostLike(postId, likeRequest.getUserId());
        return liked
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}