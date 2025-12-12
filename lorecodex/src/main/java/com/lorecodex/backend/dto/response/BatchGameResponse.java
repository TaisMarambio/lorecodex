package com.lorecodex.backend.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGameResponse {
    private int totalProcessed;
    private int successCount;
    private int failureCount;
    private List<GameImportResult> results;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameImportResult {
        private String title;
        private boolean success;
        private String message;
        private Long gameId;
    }
}
