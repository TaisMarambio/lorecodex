package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface RoleService {
    Optional<Role> getRoleByName(String name);
}