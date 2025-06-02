package com.lorecodex.backend.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long parentId; // null si es comentario raíz
}
