package social.com.chatservice.message.service;

import org.springframework.stereotype.Service;
import social.com.chatservice.message.model.Message;
import social.com.chatservice.message.repository.MessageRepository;
import social.com.chatservice.web.dto.CreateMessageRequest;
import java.time.LocalDateTime;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void createMessage(CreateMessageRequest createMessageRequest) {
        Message build = Message.builder()
                .text(createMessageRequest.getText())
                .senderId(createMessageRequest.getSenderId())
                .receiverId(createMessageRequest.getReceiverId())
                .createdAt(LocalDateTime.now())
                .build();

        messageRepository.save(build);
    }
}