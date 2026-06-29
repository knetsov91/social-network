package social.com.chatservice.chat.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.chat.repository.ChatRepository;
import social.com.chatservice.message.model.Message;
import social.com.chatservice.user.client.UserClient;
import social.com.chatservice.user.client.dto.UserResponse;
import social.com.chatservice.web.dto.CreateChatRequest;
import social.com.chatservice.web.dto.ParticipantResponse;
import social.com.chatservice.web.dto.UserChatResponse;

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

    @Mock
    private UserClient userClient;

    @Test
    void test_getUserChats_whenUserFound_thenReturnsMappedChatsWithParticipants() {
        UUID userId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();

        Chat chat = new Chat();
        chat.setId("chat-123");
        chat.setCreatedBy(userId);
        chat.setParticipants(List.of(participantId));

        when(chatRepository.findByParticipantsContains(userId)).thenReturn(Optional.of(List.of(chat)));
        when(userClient.getUserById(participantId)).thenReturn(new UserResponse(participantId, "test-user"));

        List<UserChatResponse> result = chatService.getUserChats(userId);

        assertEquals(1, result.size());
        assertEquals("chat-123", result.get(0).getChatId());
        assertEquals(userId, result.get(0).getCreatedBy());
        ParticipantResponse expectedParticipant = new ParticipantResponse();
        expectedParticipant.setId(participantId);
        expectedParticipant.setUsername("test-user");
        assertEquals(List.of(expectedParticipant), result.get(0).getParticipants());
    }

    @Test
    void test_getUserChats_whenUserNotFound_thenThrowsException() {
        UUID userId = UUID.randomUUID();

        when(chatRepository.findByParticipantsContains(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chatService.getUserChats(userId));
    }

    @Test
    void test_createChat_whenChatAlreadyExists_thenThrowsException() {
        UUID createdBy = UUID.randomUUID();
        UUID participant = UUID.randomUUID();
        List<UUID> participants = List.of(createdBy, participant);

        CreateChatRequest request = new CreateChatRequest();
        request.setCreatedBy(createdBy);
        request.setParticipants(participants);

        Chat existing = new Chat();
        existing.setParticipants(participants);

        when(chatRepository.findByParticipants(participants, participants.size())).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> chatService.createChat(request));
    }

    @Test
    void test_createChat_whenNoChatExists_thenSavesChat() {
        UUID createdBy = UUID.randomUUID();
        UUID participant = UUID.randomUUID();
        List<UUID> participants = List.of(createdBy, participant);

        CreateChatRequest request = new CreateChatRequest();
        request.setCreatedBy(createdBy);
        request.setParticipants(participants);

        when(chatRepository.findByParticipants(participants, participants.size())).thenReturn(Optional.empty());

        chatService.createChat(request);

        ArgumentCaptor<Chat> captor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(captor.capture());

        assertEquals(createdBy, captor.getValue().getCreatedBy());
        assertEquals(participants, captor.getValue().getParticipants());
    }

    @Test
    void test_getChatById_whenChatFound_thenReturnsChat() {
        String chatId = "chat-123";

        Chat chat = new Chat();
        chat.setParticipants(List.of(UUID.randomUUID()));

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        Chat result = chatService.getChatById(chatId);

        assertEquals(chat, result);
    }

    @Test
    void test_getChatById_whenChatNotFound_thenThrowsException() {
        String chatId = "chat-123";

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chatService.getChatById(chatId));
    }

    @Test
    void test_addMessageToChat_whenMessageProvided_thenAddsMessageAndSavesChat() {
        String chatId = "chat-123";

        Chat chat = new Chat();
        chat.setParticipants(List.of(UUID.randomUUID()));

        Message message = Message.builder()
                .text("hello")
                .senderId(UUID.randomUUID())
                .receiverId(UUID.randomUUID())
                .build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        chatService.addMessageToChat(chatId, message);

        ArgumentCaptor<Chat> captor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(captor.capture());

        assertEquals(List.of(message), captor.getValue().getMessages());
    }
}
