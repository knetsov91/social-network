package social.com.chatservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import social.com.chatservice.message.service.MessageService;
import social.com.chatservice.web.dto.CreateMessageRequest;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity createMessage(@RequestBody CreateMessageRequest createMessageRequest) {
        messageService.createMessage(createMessageRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}