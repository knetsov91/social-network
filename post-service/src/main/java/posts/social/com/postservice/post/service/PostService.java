package posts.social.com.postservice.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import posts.social.com.postservice.Like;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.web.dto.PostCreateRequest;
import posts.social.com.postservice.post.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {
    private static final String TOPIC_LIKES = "likes-topic";

    private PostRepository postRepository;
    private KafkaTemplate<String, Like> kafkaTemplate;

    public PostService(PostRepository postRepository, KafkaTemplate<String, Like> kafkaTemplate) {
        this.postRepository = postRepository;
        this.kafkaTemplate = kafkaTemplate;
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

    public void togglePostLike(UUID postId, UUID userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post with id: " + postId + " not found");
        }

        Post post = postOpt.get();
        List<UUID> postLikes = post.getLikes();
        if (postLikes.contains(userId)) {
            postLikes.remove(userId);
        } else {
            postLikes.add(userId);
            Like like = new Like(postId, userId, LocalDateTime.now());
            kafkaTemplate.send(TOPIC_LIKES, like);
        }

        postRepository.save(post);
    }

    public Page<Post> getFeedPosts(int pageNumber, int pageSize) {
        Pageable p = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt"));
        return postRepository.findAll(p);//(Sort.by("createdAt"));
    }
}