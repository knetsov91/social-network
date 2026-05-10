package social.com.chatservice.message.service;

import org.springframework.stereotype.Service;
import social.com.chatservice.chat.service.ChatService;
import social.com.chatservice.message.model.Message;
import social.com.chatservice.message.repository.MessageRepository;
import social.com.chatservice.web.Mapper;
import social.com.chatservice.web.dto.CreateMessageRequest;
import social.com.chatservice.web.dto.MessageResponse;
import java.time.LocalDateTime;

@Service
public class MessageService {

    private MessageRepository messageRepository;
    private ChatService chatService;

    public MessageService(MessageRepository messageRepository, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
    }

    public MessageResponse createMessage(CreateMessageRequest createMessageRequest) {
        Message message = Message.builder()
                .text(createMessageRequest.getText())
                .senderId(createMessageRequest.getSenderId())
                .receiverId(createMessageRequest.getReceiverId())
                .createdAt(LocalDateTime.now())
                .build();

        Message saved = messageRepository.save(message);
        chatService.addMessageToChat(createMessageRequest.getChatId(), saved);

        return Mapper.mapMessageToMessageResponse(saved);
    }
}
