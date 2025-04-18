package social.com.chatservice.chat.service;

import org.springframework.stereotype.Service;
import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.chat.repository.ChatRepository;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<Chat> getUserChats(UUID userId) {
        return chatRepository.findByParticipantsContains(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));
    }
}
