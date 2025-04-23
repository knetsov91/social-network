package social.com.chatservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.chat.repository.ChatRepository;
import social.com.chatservice.chat.service.ChatService;
import social.com.chatservice.web.dto.ChatResponse;
import social.com.chatservice.web.dto.CreateChatRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    private ChatRepository chatRepository;
    private ChatService chatService;

    public ChatController(ChatRepository chatRepository, ChatService chatService) {
        this.chatRepository = chatRepository;
        this.chatService = chatService;
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable String chatId) {
        Chat chat = chatService.getChatById(chatId);
        ChatResponse chatResponse = Mapper.mapChatToChatResponse(chat);

        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserChats(@PathVariable UUID userId) {
        List<Chat> chats = chatService.getUserChats(userId);
        return ResponseEntity.ok(chats);
    }

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody CreateChatRequest createChatRequest) {
        chatService.createChat(createChatRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}