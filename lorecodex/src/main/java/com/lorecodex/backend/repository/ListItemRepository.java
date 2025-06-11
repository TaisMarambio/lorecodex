package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListItemRepository extends JpaRepository<ListItem, Long> {
    List<ListItem> findByUserListIdOrderByPositionAsc(Long userListId);
}
