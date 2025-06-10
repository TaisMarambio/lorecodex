package com.lorecodex.backend.dto.request;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class GameNoteRequest {
    private String content;
}
