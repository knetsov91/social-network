package social.com.chatservice.message.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.chat.service.ChatService;
import social.com.chatservice.message.model.Message;
import social.com.chatservice.message.repository.MessageRepository;
import social.com.chatservice.web.dto.MessageResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceUTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatService chatService;

    @Test
    void test_getMessagesByChatId_happyPath() {
        String chatId = "chat-123";
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Message message = Message.builder()
                .id("msg-1")
                .text("hello")
                .senderId(senderId)
                .receiverId(receiverId)
                .createdAt(now)
                .build();

        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setMessages(List.of(message));

        when(chatService.getChatById(chatId)).thenReturn(chat);

        List<MessageResponse> result = messageService.getMessagesByChatId(chatId);

        assertEquals(1, result.size());
        assertEquals("msg-1", result.get(0).getId());
        assertEquals("hello", result.get(0).getText());
        assertEquals(senderId, result.get(0).getSenderId());
        assertEquals(receiverId, result.get(0).getReceiverId());
        assertEquals(now, result.get(0).getCreatedAt());
    }

    @Test
    void test_getMessagesByChatId_whenChatNotFound_thenThrowException() {
        String chatId = "chat-123";

        when(chatService.getChatById(chatId)).thenThrow(new RuntimeException("Chat not found"));

        assertThrows(RuntimeException.class, () -> messageService.getMessagesByChatId(chatId));
    }
}
