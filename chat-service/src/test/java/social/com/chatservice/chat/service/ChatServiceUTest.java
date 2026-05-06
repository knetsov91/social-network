package social.com.chatservice.chat.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.chat.repository.ChatRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
