package social.com.userservice.follow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import social.com.userservice.follow.model.Follow;
import social.com.userservice.follow.repository.FollowRepository;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowServiceUTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private FollowRepository followRepository;

    @Test
    void test_follow_happyPath() {
        Follow follow = new Follow();
        UUID followerId = UUID.randomUUID();
        follow.setFollowerId(followerId);
        UUID followeeId = UUID.randomUUID();
        follow.setFolloweeId(followeeId);

        Mockito.when(followRepository.save(follow)).thenReturn(follow);
        followRepository.save(follow);

        verify(followRepository, times(1)).save(follow);
        assertEquals(followerId, follow.getFollowerId());
        assertEquals(followeeId, follow.getFolloweeId());
    }
}