package com.lorecodex.backend.dto.response;

import com.lorecodex.backend.model.ListItemType;
import lombok.Data;

@Data
public class ListItemResponse {
    private Long id;
    private ListItemType type;
    private Long referenceId;
    private int position;
}
