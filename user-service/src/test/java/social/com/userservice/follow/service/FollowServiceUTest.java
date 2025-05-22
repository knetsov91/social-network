package social.com.userservice.follow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import social.com.userservice.common.TimeProvider;
import social.com.userservice.follow.model.Follow;
import social.com.userservice.follow.repository.FollowRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceUTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private TimeProvider timeProvider;

    @Test
    void test_follow_happyPath() {
        UUID followerId = UUID.randomUUID();
        UUID followeeId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFolloweeId(followeeId);
        follow.setCreatedAt(now);

        when(timeProvider.now()).thenReturn(now);
        when(followRepository.save(any())).thenReturn(follow);

        followService.follow(followerId, followeeId);

        verify(followRepository, times(1)).save(follow);
    }
}