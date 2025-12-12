package com.lorecodex.backend.dto.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchGameRequest {
    private List<GameRequest> games;
}
