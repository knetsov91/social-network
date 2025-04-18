package social.com.chatservice.message.service;

import org.springframework.stereotype.Service;
import social.com.chatservice.chat.service.ChatService;
import social.com.chatservice.message.model.Message;
import social.com.chatservice.message.repository.MessageRepository;
import social.com.chatservice.web.dto.CreateMessageRequest;
import java.time.LocalDateTime;

@Service
public class MessageService {

    private MessageRepository messageRepository;
    private ChatService chatService;

    public MessageService(MessageRepository messageRepository, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
    }

    public void createMessage(CreateMessageRequest createMessageRequest) {
        Message build = Message.builder()
                .text(createMessageRequest.getText())
                .senderId(createMessageRequest.getSenderId())
                .receiverId(createMessageRequest.getReceiverId())
                .createdAt(LocalDateTime.now())
                .build();

        Message message = messageRepository.save(build);

        chatService.addMessageToChat(createMessageRequest.getChatId(), message);
    }
}