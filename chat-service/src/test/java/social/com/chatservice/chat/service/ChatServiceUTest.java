package social.com.chatservice.chat.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.chat.repository.ChatRepository;
import social.com.chatservice.web.dto.CreateChatRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceUTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRepository chatRepository;

    @Test
    void test_getUserChats_happyPath() {
        UUID userId = UUID.randomUUID();

        Chat chat1 = new Chat();
        chat1.setParticipants(List.of(userId));

        Chat chat2 = new Chat();
        chat2.setParticipants(List.of(userId));

        when(chatRepository.findByParticipantsContains(userId)).thenReturn(Optional.of(List.of(chat1, chat2)));

        List<Chat> result = chatService.getUserChats(userId);

        assertEquals(List.of(chat1, chat2), result);
    }

    @Test
    void test_getUserChats_whenNotFound_thenThrowException() {
        UUID userId = UUID.randomUUID();

        when(chatRepository.findByParticipantsContains(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chatService.getUserChats(userId));
    }

    @Test
    void test_createChat_happyPath() {
        UUID participant1 = UUID.randomUUID();
        UUID participant2 = UUID.randomUUID();

        CreateChatRequest request = new CreateChatRequest();
        request.setParticipants(List.of(participant1, participant2));

        chatService.createChat(request);

        ArgumentCaptor<Chat> captor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(captor.capture());

        assertEquals(List.of(participant1, participant2), captor.getValue().getParticipants());
    }

    @Test
    void test_getChatById_happyPath() {
        String chatId = "chat-123";

        Chat chat = new Chat();
        chat.setParticipants(List.of(UUID.randomUUID()));

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        Chat result = chatService.getChatById(chatId);

        assertEquals(chat, result);
    }

    @Test
    void test_getChatById_whenNotFound_thenThrowException() {
        String chatId = "chat-123";

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chatService.getChatById(chatId));
    }
}
