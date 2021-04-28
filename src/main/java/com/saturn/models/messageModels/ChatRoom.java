package com.saturn.models.messageModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность чат-комнаты
 */
@Entity
@Table(name = "chat_rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {
    /**
     * Автозаполняемый айдишник
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

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
}
