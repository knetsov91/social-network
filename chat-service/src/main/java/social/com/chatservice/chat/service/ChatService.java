package social.com.chatservice.chat.service;

import org.springframework.stereotype.Service;
import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.chat.repository.ChatRepository;
import social.com.chatservice.message.model.Message;
import social.com.chatservice.user.client.UserClient;
import social.com.chatservice.user.client.dto.UserResponse;
import social.com.chatservice.web.dto.CreateChatRequest;
import social.com.chatservice.web.dto.ParticipantResponse;
import social.com.chatservice.web.dto.UserChatResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private ChatRepository chatRepository;
    private UserClient userClient;

    public ChatService(ChatRepository chatRepository, UserClient userClient) {
        this.chatRepository = chatRepository;
        this.userClient = userClient;
    }

    public List<UserChatResponse> getUserChats(UUID userId) {
        List<Chat> chats = chatRepository.findByParticipantsContains(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        return chats.stream().map(chat -> {
            List<ParticipantResponse> participants = chat.getParticipants().stream()
                    .map(participantId -> {
                        UserResponse user = userClient.getUserById(participantId);
                        ParticipantResponse participant = new ParticipantResponse();
                        participant.setId(user.userId());
                        participant.setUsername(user.username());
                        return participant;
                    })
                    .collect(Collectors.toList());

            UserChatResponse response = new UserChatResponse();
            response.setChatId(chat.getId());
            response.setCreatedBy(chat.getCreatedBy());
            response.setParticipants(participants);
            response.setCreatedAt(chat.getCreatedAt());
            response.setUpdatedAt(chat.getUpdatedAt());
            return response;
        }).collect(Collectors.toList());
    }

    public void createChat(CreateChatRequest createChatRequest) {
        Chat chat = new Chat();
        chat.setCreatedBy(createChatRequest.getCreatedBy());
        chat.setParticipants(createChatRequest.getParticipants());
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());

         chatRepository.save(chat);
    }

    public Chat getChatById(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat with id " + chatId + " not found"));
    }

    public void addMessageToChat(String chatId, Message message) {
        Chat chat = getChatById(chatId);
        chat.getMessages().add(message);

        chatRepository.save(chat);
    }
}
