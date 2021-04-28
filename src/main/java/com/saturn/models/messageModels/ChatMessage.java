package com.saturn.models.messageModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Сущность сообщения
 */
@Entity
@Table(name = "chat_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    /**
     * Автозаполняемый айдишник
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Айди чата (создается по айдишникам получателя и отправителя)
     */
    private String chatId;
    /**
     * Айди отправителя
     */
    private String senderId;
    /**
     * Айди получателя
     */
    private String recipientId;
    /**
     * Имя отправителя
     */
    private String senderName;
    /**
     * Имя получателя
     */
    private String recipientName;
    /**
     * Тело сообщения
     */
    private String content;
    /**
     * Время создания сообщения
     */
    private Date timestamp;

    /**
     * Прочитан / не прочитан (пока не используется)
     * @deprecated
     */
    private MessageStatus status;
}