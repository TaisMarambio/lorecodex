package com.lorecodex.backend.dto.request;

import lombok.Data;

@Data
public class ReorderItemRequest {
    private Long itemId;
    private int newPosition;
}
