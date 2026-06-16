package posts.social.com.postservice.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import posts.social.com.postservice.Like;
import posts.social.com.postservice.client.UserClient;
import posts.social.com.postservice.outbox.OutboxEvent;
import posts.social.com.postservice.outbox.OutboxEventRepository;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.web.dto.PostCreateRequest;
import posts.social.com.postservice.post.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    private static final String TOPIC_LIKES = "likes-topic";
    private static final String CACHE_USER_POSTS = "user-posts";

    private final PostRepository postRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;
    private final UserClient userClient;

    public PostService(PostRepository postRepository, OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper, CacheManager cacheManager, UserClient userClient) {
        this.postRepository = postRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.cacheManager = cacheManager;
        this.userClient = userClient;
    }

    @CacheEvict(value = CACHE_USER_POSTS, key = "#postCreateRequest.authorId")
    public void create(PostCreateRequest postCreateRequest) {
        Post post = Post.builder()
                .authorId(postCreateRequest.getAuthorId())
                .content(postCreateRequest.getContent())
                .title(postCreateRequest.getTitle())
                .build();
        postRepository.save(post);
    }

    @Cacheable(value = CACHE_USER_POSTS, key = "#authorId")
    public List<Post> getPosts(UUID authorId) {
        return postRepository.findByAuthorId(authorId)
                .orElse(List.of());
    }

    public Page<Post> getFeed(UUID userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<UUID> followings = userClient.getFollowings(userId);
        if (followings.isEmpty()) {
            return Page.empty(pageable);
        }
        return postRepository.findByAuthorIdIn(followings, pageable);
    }

    @Transactional
    public boolean togglePostLike(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post with id: " + postId + " not found"));

        cacheManager.getCache(CACHE_USER_POSTS).evict(post.getAuthorId());

        List<UUID> postLikes = post.getLikes();
        if (postLikes.contains(userId)) {
            postLikes.remove(userId);
            postRepository.save(post);
            return false;
        }

        postLikes.add(userId);
        postRepository.save(post);
        try {
            outboxEventRepository.save(buildOutboxEvent(new Like(postId, userId, LocalDateTime.now())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize like event", e);
        }
        return true;
    }

    private OutboxEvent buildOutboxEvent(Like like) throws JsonProcessingException {
        return OutboxEvent.builder()
                .topic(TOPIC_LIKES)
                .payload(objectMapper.writeValueAsString(like))
                .build();
    }
}
