package social.com.chatservice.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import social.com.chatservice.chat.model.Chat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    Optional<List<Chat>> findByParticipantsContains(UUID participant);

    @Query("{ 'participants': { $all: ?0, $size: ?1 } }")
    Optional<Chat> findByParticipants(List<UUID> participants, int size);
}