package posts.social.com.postservice.web;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.post.service.PostService;
import posts.social.com.postservice.web.dto.AuthorPostsResponse;
import posts.social.com.postservice.web.dto.PageablePostsResponse;
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
    public ResponseEntity post(@RequestHeader(name="Authorization") String authorization, @RequestBody PostCreateRequest postCreateRequest) {
        if (!authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        postService.create(postCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<AuthorPostsResponse>> getPosts(@RequestHeader(name="Authorization") String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //TODO: query user service to check if authorId is valid
        String token = authorization.split(" ")[1];
        UUID authorId = UUID.fromString(token);

        List<Post> posts =  postService.getPosts(authorId);
        List<AuthorPostsResponse> mapped = Mapper.mapListPostsToListAuthorPostsResponse(posts);

        return ResponseEntity.status(HttpStatus.OK).body(mapped);
    }

    @PutMapping("/{postId}/likes")
    public ResponseEntity like(@PathVariable("postId") UUID postId, @RequestHeader(name="Authorization") String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //TODO: query user service to check if authorId is valid
        String token = authorization.split(" ")[1];
        UUID userId = UUID.fromString(token);

        boolean exists = postService.postLikeExists(postId, userId);
        postService.togglePostLike(postId, userId);
        if (exists) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/feed")
    public PageablePostsResponse paginatedPosts(@RequestParam("page") int page,
                                     @RequestParam("size") int size) {
        Page<Post> feed = postService.getFeedPosts(page, size);
        PageablePostsResponse pageablePostsResponse = Mapper.mapPageablePostsToPageablePostsResponse(feed);


        return pageablePostsResponse;
    }
}