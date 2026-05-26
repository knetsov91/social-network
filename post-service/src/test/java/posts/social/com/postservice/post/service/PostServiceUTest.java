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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.cache.Cache;

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

    @Test
    void test_getPosts_whenNoPostsFoundForAuthor_thenReturnsEmptyList() {
        UUID authorId = UUID.randomUUID();

        when(postRepository.findByAuthorId(authorId)).thenReturn(Optional.empty());

        List<Post> result = postService.getPosts(authorId);

        assertTrue(result.isEmpty());
    }

    @Test
    void test_getPosts_whenPostsFoundForAuthor_thenReturnsThem() {
        UUID authorId = UUID.randomUUID();

        Post post = Post.builder().authorId(authorId).title("title").content("content").build();

        when(postRepository.findByAuthorId(authorId)).thenReturn(Optional.of(List.of(post)));

        List<Post> result = postService.getPosts(authorId);

        assertEquals(1, result.size());
        assertEquals(post, result.get(0));
    }

    @Test
    void test_togglePostLike_whenPostNotFound_thenThrowsException() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.togglePostLike(postId, userId));
    }

    @Test
    void test_togglePostLike_whenUserHasNotLikedPost_thenAddsLikeAndPublishesKafkaEvent() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Post post = Post.builder().authorId(authorId).title("title").content("content").likes(new ArrayList<>()).build();

        Cache cache = mock(Cache.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(cacheManager.getCache("user-posts")).thenReturn(cache);

        boolean result = postService.togglePostLike(postId, userId);

        assertTrue(result);
        assertTrue(post.getLikes().contains(userId));
        verify(kafkaTemplate).send(eq("likes-topic"), any(Like.class));
        verify(postRepository).save(post);
    }
}
