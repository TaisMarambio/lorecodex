package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ListItemRequest;
import com.lorecodex.backend.dto.request.ReorderItemRequest;
import com.lorecodex.backend.dto.request.UserListRequest;
import com.lorecodex.backend.dto.response.UserListResponse;
import com.lorecodex.backend.service.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lists")
@RequiredArgsConstructor
public class UserListController {

    private final UserListService userListService;

    @PostMapping("/{userId}/create")
    public ResponseEntity<UserListResponse> createList(
            @PathVariable Long userId,
            @RequestBody UserListRequest request
    ) {
        return ResponseEntity.ok(userListService.createList(userId, request));
    }

    @GetMapping("/user/{userId}/get-lists")
    public ResponseEntity<List<UserListResponse>> getListsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userListService.getListsForUser(userId));
    }

    @PutMapping("/{listId}/update")
    public ResponseEntity<UserListResponse> updateList(
            @PathVariable Long listId,
            @RequestBody UserListRequest request
    ) {
        return ResponseEntity.ok(userListService.updateList(listId, request));
    }

    @DeleteMapping("/{listId}/delete")
    public ResponseEntity<Void> deleteList(@PathVariable Long listId) {
        userListService.deleteList(listId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listId}/items/add")
    public ResponseEntity<Void> addItemToList(
            @PathVariable Long listId,
            @RequestBody ListItemRequest request
    ) {
        userListService.addItemToList(listId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{listId}/items/{itemId}/remove-item")
    public ResponseEntity<Void> removeItemFromList(
            @PathVariable Long listId,
            @PathVariable Long itemId
    ) {
        userListService.removeItemFromList(listId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{listId}/items/reorder")
    public ResponseEntity<Void> reorderItems(
            @PathVariable Long listId,
            @RequestBody List<ReorderItemRequest> request
    ) {
        userListService.reorderItems(listId, request);
        return ResponseEntity.ok().build();
    }
}
