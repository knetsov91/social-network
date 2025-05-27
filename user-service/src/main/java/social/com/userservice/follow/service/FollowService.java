package social.com.userservice.follow.service;

import org.springframework.stereotype.Service;
import social.com.userservice.common.TimeProvider;
import social.com.userservice.follow.model.Follow;
import social.com.userservice.follow.repository.FollowRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final TimeProvider timeProvider;

    public FollowService(FollowRepository followRepository, TimeProvider timeProvider) {
        this.followRepository = followRepository;
        this.timeProvider = timeProvider;
    }

    public void follow(UUID followerId, UUID followeeId) {

        Follow follow = new Follow();
        follow.setFolloweeId(followeeId);
        follow.setFollowerId(followerId);
        follow.setCreatedAt(timeProvider.now());

        followRepository.save(follow);
    }

    public List<UUID> getUserFollowings(UUID userId) {
        Optional<List<Follow>> byFollowerId = followRepository.findByFollowerId(userId);
        if (byFollowerId.isEmpty()) {}

        return byFollowerId.get().stream()
                .map(Follow::getFolloweeId)
                .collect(Collectors.toList());
    }
}