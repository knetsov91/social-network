package posts.social.com.postservice.post.service;

import org.springframework.stereotype.Service;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.web.dto.PostCreateRequest;
import posts.social.com.postservice.post.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void create(PostCreateRequest postCreateRequest) {
        Post post = Post.builder()
                .authorId(postCreateRequest.getAuthorId())
                .content(postCreateRequest.getContent())
                .title(postCreateRequest.getTitle())
                .build();
        postRepository.save(post);
    }

    public List<Post> getPosts(UUID authorId) {
        Optional<List<Post>> posts = postRepository
                .findByAuthorId(authorId);
        
        if (posts.isEmpty() || posts.get().isEmpty()) {
            throw new RuntimeException("There are not posts for user with id: " + authorId);
        }

        return posts.get();
    }

    public boolean postLikeExists(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("No post with id: " + postId));
        return post.getLikes().contains(userId);
    }
}