package com.saturn.repositories;

import com.saturn.models.messageModels.ChatMessage;
import com.saturn.models.messageModels.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Long countBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status);
    List<ChatMessage> findByChatIdOrderByTimestamp(String chatId);
}
