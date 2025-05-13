package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserListRepository extends JpaRepository<UserList, Long> {
    List<UserList> findByUserId(Long userId);
}
