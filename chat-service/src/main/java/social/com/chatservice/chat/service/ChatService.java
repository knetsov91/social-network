package social.com.chatservice.chat.service;

import org.springframework.stereotype.Service;
import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.chat.repository.ChatRepository;
import social.com.chatservice.web.dto.CreateChatRequest;
import java.time.LocalDateTime;
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

    public void createChat(CreateChatRequest createChatRequest) {
        Chat chat = new Chat();
        chat.setParticipants(createChatRequest.getParticipants());
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());

         chatRepository.save(chat);
    }

    public Chat getChatById(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat with id " + chatId + " not found"));
    }
}
