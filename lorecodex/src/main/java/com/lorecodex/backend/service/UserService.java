package com.lorecodex.backend.service;

import com.lorecodex.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByUsername(String username);
    User createUser(User user);
    User updateUser(Integer id, User user);
    void deleteUser(Integer id);
}
