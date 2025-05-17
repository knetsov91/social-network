package social.com.userservice.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import social.com.userservice.follow.model.Follow;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {
}
