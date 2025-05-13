package com.lorecodex.backend.dto.request;

import com.lorecodex.backend.model.ListItemType;
import lombok.Data;

@Data
public class ListItemRequest {
    private ListItemType type;
    private Long referenceId;
    private int position;
}
