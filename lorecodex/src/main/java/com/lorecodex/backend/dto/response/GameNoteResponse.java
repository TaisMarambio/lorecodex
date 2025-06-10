package com.lorecodex.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameNoteResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
}
