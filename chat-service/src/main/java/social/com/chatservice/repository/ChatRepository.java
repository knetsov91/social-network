package social.com.chatservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import social.com.chatservice.model.Chat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends MongoRepository<Chat, UUID> {

    Optional<List<Chat>> findByOwnerId(UUID userId);
}