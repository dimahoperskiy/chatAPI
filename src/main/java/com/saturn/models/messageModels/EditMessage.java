package com.saturn.models.messageModels;

import lombok.Data;

@Data
public class EditMessage {
    private Long id;
    private String recipient;
    private String content;
}
