package social.com.chatservice.web;

import social.com.chatservice.chat.model.Chat;
import social.com.chatservice.web.dto.ChatResponse;
import social.com.chatservice.web.dto.MessageResponse;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static ChatResponse mapChatToChatResponse(Chat chat) {
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setId(chat.getId());
        chatResponse.setParticipants(chat.getParticipants());
        chatResponse.setCreatedAt(chat.getCreatedAt());
        chatResponse.setUpdatedAt(chat.getUpdatedAt());

        List<MessageResponse> messageResponses = new ArrayList<>();

        chat.getMessages().forEach(message -> {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setId(message.getId());
            messageResponse.setText(message.getText());
            messageResponse.setCreatedAt(message.getCreatedAt());
            messageResponse.setUpdatedAt(message.getUpdatedAt());
            messageResponse.setSenderId(message.getSenderId());
            messageResponse.setReceiverId(message.getReceiverId());
            messageResponses.add(messageResponse);
        });

        chatResponse.setMessages(messageResponses);

        return chatResponse;
    }
}
