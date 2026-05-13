package posts.social.com.postservice.post.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import posts.social.com.postservice.Like;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.web.dto.PostCreateRequest;
import posts.social.com.postservice.post.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
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
        return postRepository.findByAuthorId(authorId)
                .orElse(List.of());
    }

    public boolean togglePostLike(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post with id: " + postId + " not found"));

        List<UUID> postLikes = post.getLikes();
        if (postLikes.contains(userId)) {
            postLikes.remove(userId);
            postRepository.save(post);
            return false;
        }

        postLikes.add(userId);
        kafkaTemplate.send(TOPIC_LIKES, new Like(postId, userId, LocalDateTime.now()));
        postRepository.save(post);
        return true;
    }
}