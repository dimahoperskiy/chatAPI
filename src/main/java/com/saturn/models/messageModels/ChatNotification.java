package com.saturn.models.messageModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель уведомления о сообщении
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    /**
     * Айди (равно ацйди сообщения)
     */
    private Long id;
    /**
     * Айди отправителя
     */
    private String senderId;
    /**
     * Имя отправителя
     */
    private String senderName;
    /**
     * Удалено или нет
     */
    private Boolean deleted;
    /**
     * Обновлено или нет
     */
    private Boolean updated;
}
