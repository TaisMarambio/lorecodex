package com.lorecodex.backend.service;

import com.lorecodex.backend.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    UserDetails loadUserByEmail(String email);
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByUsername(String username);
    User createUser(User user);
    User updateUser(Integer id, User user);
    void deleteUser(Integer id);
    Iterable<User> getAllUsers();

}
