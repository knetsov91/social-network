package social.com.chatservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import social.com.chatservice.chat.repository.ChatRepository;
import social.com.chatservice.chat.service.ChatService;
import social.com.chatservice.web.dto.CreateChatRequest;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    private ChatRepository chatRepository;
    private ChatService chatService;

    public ChatController(ChatRepository chatRepository, ChatService chatService) {
        this.chatRepository = chatRepository;
        this.chatService = chatService;
    }

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody CreateChatRequest createChatRequest) {
        chatService.createChat(createChatRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}