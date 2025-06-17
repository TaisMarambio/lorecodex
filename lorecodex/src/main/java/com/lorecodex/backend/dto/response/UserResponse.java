package com.lorecodex.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}