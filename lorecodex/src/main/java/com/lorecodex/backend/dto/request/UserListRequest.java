package com.lorecodex.backend.dto.request;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class UserListRequest {
    private String title;
    private String description;
    private List<ListItemRequest> items;
}
