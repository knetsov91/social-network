package social.com.chatservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import social.com.chatservice.message.service.MessageService;
import social.com.chatservice.web.dto.CreateMessageRequest;
import social.com.chatservice.web.dto.MessageResponse;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private MessageService messageService;
    private SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@RequestBody CreateMessageRequest createMessageRequest) {
        MessageResponse response = messageService.createMessage(createMessageRequest);
        messagingTemplate.convertAndSend("/topic/chat/" + createMessageRequest.getChatId(), response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
