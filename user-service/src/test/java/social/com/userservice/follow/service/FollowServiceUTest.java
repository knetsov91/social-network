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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void test_getUserFollowings_happyPath() {
        UUID followerId = UUID.randomUUID();
        UUID followeeId1 = UUID.randomUUID();
        UUID followeeId2 = UUID.randomUUID();

        Follow follow1 = new Follow();
        follow1.setFollowerId(followerId);
        follow1.setFolloweeId(followeeId1);

        Follow follow2 = new Follow();
        follow2.setFollowerId(followerId);
        follow2.setFolloweeId(followeeId2);

        when(followRepository.findByFollowerId(followerId)).thenReturn(Optional.of(List.of(follow1, follow2)));

        List<UUID> result = followService.getUserFollowings(followerId);

        assertEquals(List.of(followeeId1, followeeId2), result);
    }
}