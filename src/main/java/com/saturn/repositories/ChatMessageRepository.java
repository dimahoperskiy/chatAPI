package com.saturn.repositories;

import com.saturn.models.messageModels.ChatMessage;
import com.saturn.models.messageModels.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс для работы с БД для ChatMessage
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    /**
     * Метод для счета сообщений
     * @param senderId Айди отправителя
     * @param recipientId Айди получателя
     * @param status Статус сообщения
     * @return Long
     */
    Long countBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status);

    /**
     * Метод для возврата сообщений чат-комнаты и их сортировки по дате и времени
     * @param chatId Айди чата
     * @return List<ChatMessage>
     */
    List<ChatMessage> findByChatIdOrderByTimestamp(String chatId);
}
