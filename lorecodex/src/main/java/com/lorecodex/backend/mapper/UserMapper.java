// Agrego un asignador de usuarios para convertir entre entidades y DTO
package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.response.UserResponse;
import com.lorecodex.backend.model.Role;
import com.lorecodex.backend.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        if (user.getRoles() != null) {
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
            dto.setRoles(roleNames);
        }

        return dto;
    }

    public List<UserResponse> toDTOList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}