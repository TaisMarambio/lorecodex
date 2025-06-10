package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.ListItemRequest;
import com.lorecodex.backend.dto.request.ReorderItemRequest;
import com.lorecodex.backend.dto.request.UserListRequest;
import com.lorecodex.backend.dto.response.ListItemResponse;
import com.lorecodex.backend.dto.response.UserListResponse;

import java.util.List;

public interface UserListService {

    UserListResponse createList(Long userId, UserListRequest request);

    List<UserListResponse> getListsForUser(Long userId);

    UserListResponse updateList(Long listId, UserListRequest request);

    void deleteList(Long listId);

    void addItemToList(Long listId, ListItemRequest request);

    void removeItemFromList(Long listId, Long itemId);

    void reorderItems(Long listId, List<ReorderItemRequest> newOrder);

    UserListResponse getListById(Long listId);

    List<UserListResponse> getAllLists();
}
