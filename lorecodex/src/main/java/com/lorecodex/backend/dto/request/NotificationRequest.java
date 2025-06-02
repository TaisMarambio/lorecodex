package com.lorecodex.backend.dto.request;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long recipientId;
    private String message;
}
