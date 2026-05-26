package posts.social.com.postservice.post.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import posts.social.com.postservice.Like;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.post.repository.PostRepository;
import posts.social.com.postservice.web.dto.PostCreateRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceUTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private KafkaTemplate<String, Like> kafkaTemplate;

    @Mock
    private CacheManager cacheManager;

    @Test
    void test_create_whenValidPostCreateRequest_thenSavesPostWithCorrectFields() {
        UUID authorId = UUID.randomUUID();

        PostCreateRequest request = new PostCreateRequest();
        request.setAuthorId(authorId);
        request.setTitle("title");
        request.setContent("content");

        postService.create(request);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());

        assertEquals(authorId, captor.getValue().getAuthorId());
        assertEquals("title", captor.getValue().getTitle());
        assertEquals("content", captor.getValue().getContent());
    }
}
