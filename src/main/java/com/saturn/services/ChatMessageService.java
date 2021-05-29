package com.saturn.services;

import com.saturn.models.messageModels.ChatMessage;
import com.saturn.models.messageModels.ChatRoom;
import com.saturn.models.messageModels.MessageStatus;
import com.saturn.repositories.ChatMessageRepository;
import com.saturn.repositories.ChatRoomRepository;
import org.hibernate.Criteria;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId) {
        return chatMessageRepository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);
        List<ChatMessage> messages = chatId.map(cId ->
                chatMessageRepository.findByChatIdOrderByTimestamp(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }
        return messages;
    }

    public Optional<ChatMessage> findById(Long id) {
        return chatMessageRepository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return chatMessageRepository.save(chatMessage);
                });
    }

    public void deleteMessage(Long id) {
        ChatMessage message = chatMessageRepository.findById(id).orElseThrow();
        chatMessageRepository.delete(message);
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);
        List<ChatMessage> messages = chatMessageRepository.findByChatIdOrderByTimestamp(chatId.toString());
        messages.forEach(msg -> {
            msg.setStatus(status);
            chatMessageRepository.save(msg);
        });
    }
}