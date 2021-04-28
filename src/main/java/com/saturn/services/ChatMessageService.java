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

/**
 * Сервис сообщений
 */
@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private ChatRoomService chatRoomService;

    /**
     * Метод для сохранения сообщения в БД
     * @param chatMessage Сообщение
     * @return Сообщение
     */
    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    /**
     * @deprecated
     */
    public long countNewMessages(String senderId, String recipientId) {
        return chatMessageRepository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    /**
     * Метод для возврата всех сообщений из чат-комнаты
     * @param senderId Айди отправителя
     * @param recipientId Айди получаетеля
     * @return Список сообщениц
     */
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);
        List<ChatMessage> messages = chatId.map(cId ->
                chatMessageRepository.findByChatIdOrderByTimestamp(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }
        return messages;
    }

    /**
     * Метод для поиска сообщения по айди
     * @param id Айди сообщения
     * @return Сообщение
     */
    public ChatMessage findById(Long id) {
        return chatMessageRepository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return chatMessageRepository.save(chatMessage);
                })
                .orElseThrow();
    }

    /**
     * Метод для удаления сообщения
     * @param id Айди сообщения
     */
    public void deleteMessage(Long id) {
        ChatMessage message = chatMessageRepository.findById(id).orElseThrow();
        chatMessageRepository.delete(message);
    }

    /**
     * @deprecated
     */
    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);
        List<ChatMessage> messages = chatMessageRepository.findByChatIdOrderByTimestamp(chatId.toString());
        messages.forEach(msg -> {
            msg.setStatus(status);
            chatMessageRepository.save(msg);
        });
    }
}