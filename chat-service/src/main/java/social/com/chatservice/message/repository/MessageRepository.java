package social.com.chatservice.message.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import social.com.chatservice.message.model.Message;

@Repository
public interface MessageRepository  extends MongoRepository<Message, String> {
}
