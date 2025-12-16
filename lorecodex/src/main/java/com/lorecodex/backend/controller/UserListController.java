package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ListItemRequest;
import com.lorecodex.backend.dto.request.ReorderItemRequest;
import com.lorecodex.backend.dto.request.UserListRequest;
import com.lorecodex.backend.dto.response.UserListResponse;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<UserListResponse>> getListsForUser(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size) {

        List<UserListResponse> allLists = userListService.getListsForUser(userId);

        // Paginación manual
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, allLists.size());

        if (fromIndex >= allLists.size()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(allLists.subList(fromIndex, toIndex));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<UserListResponse>> getAllLists(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size) {

        List<UserListResponse> allLists = userListService.getAllLists();

        // Paginación manual
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, allLists.size());

        if (fromIndex >= allLists.size()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(allLists.subList(fromIndex, toIndex));
    }

    // FIXED: Changed endpoint to match frontend expectations
    @GetMapping("/{listId}")
    public ResponseEntity<UserListResponse> getListById(@PathVariable Long listId) {
        return ResponseEntity.ok(userListService.getListById(listId));
    }

    @PutMapping("/{listId}/update")
    public ResponseEntity<UserListResponse> updateList(
            @PathVariable Long listId,
            @RequestBody UserListRequest request
    ) {
        return ResponseEntity.ok(userListService.updateList(listId, request));
    }

    @DeleteMapping("/{listId}/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteList(
            @PathVariable Long listId,
            @AuthenticationPrincipal User currentUser
    ) {
        userListService.deleteList(listId, currentUser);
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

    @GetMapping("/{listId}/author")
    public ResponseEntity<String> getListAuthor(@PathVariable Long listId) {
        UserListResponse list = userListService.getListById(listId);
        return ResponseEntity.ok(list.getUsername());
    }
}
