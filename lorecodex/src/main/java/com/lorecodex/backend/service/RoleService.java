package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> getRoleByName(String name);
}