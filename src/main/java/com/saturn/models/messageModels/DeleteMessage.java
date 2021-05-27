package com.saturn.models.messageModels;

import lombok.Data;

@Data
public class DeleteMessage {
    private Long id;
    private String recipient;
}
