package posts.social.com.postservice.post.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import posts.social.com.postservice.Like;
import posts.social.com.postservice.post.repository.PostRepository;

import posts.social.com.postservice.web.dto.PostCreateRequest;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class PostServiceITTest {

    @Autowired
    private PostService postService;

    @MockitoSpyBean
    private PostRepository postRepository;

    @MockitoBean
    private KafkaTemplate<String, Like> kafkaTemplate;

    @Test
    void test_getPosts_whenCalledTwice_thenSecondCallReturnsCachedResult() {
        UUID authorId = UUID.randomUUID();

        postService.getPosts(authorId);
        postService.getPosts(authorId);

        verify(postRepository, times(1)).findByAuthorId(authorId);
    }

    @Test
    void test_create_whenPostCreated_thenEvictsCacheSoNextGetPostsHitsDb() {
        UUID authorId = UUID.randomUUID();

        postService.getPosts(authorId);

        PostCreateRequest request = new PostCreateRequest();
        request.setAuthorId(authorId);
        request.setTitle("title");
        request.setContent("content");
        postService.create(request);

        postService.getPosts(authorId);

        verify(postRepository, times(2)).findByAuthorId(authorId);
    }
}
