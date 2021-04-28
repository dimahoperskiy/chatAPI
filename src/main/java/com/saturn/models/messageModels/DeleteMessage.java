package com.saturn.models.messageModels;

import lombok.Data;

/**
 * Запрос на удаление сообщения
 */
@Data
public class DeleteMessage {
    private Long id;
    private String recipient;
}
