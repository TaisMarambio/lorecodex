package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListItemRepository extends JpaRepository<ListItem, Long> {
    List<ListItem> findByUserListIdOrderByPositionAsc(Long userListId);
}
