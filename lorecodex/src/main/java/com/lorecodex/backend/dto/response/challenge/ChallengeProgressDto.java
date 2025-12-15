package com.lorecodex.backend.dto.response.challenge;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeProgressDto {
    private Long challengeId;
    private double progress; // percentage 0‑100
    private int completed;
    private int total;

    @Builder.Default
    private List<Long> completedItemIds = new ArrayList<>();

    // Alias para compatibilidad con frontend
    public List<Long> getCompletedItems() {
        return completedItemIds;
    }

    public void setCompletedItems(List<Long> completedItems) {
        this.completedItemIds = completedItems;
    }
}
