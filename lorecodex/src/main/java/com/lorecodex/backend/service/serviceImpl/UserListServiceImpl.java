package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.ListItemRequest;
import com.lorecodex.backend.dto.request.ReorderItemRequest;
import com.lorecodex.backend.dto.request.UserListRequest;
import com.lorecodex.backend.dto.response.ListItemResponse;
import com.lorecodex.backend.dto.response.UserListResponse;
import com.lorecodex.backend.model.ListItem;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserList;
import com.lorecodex.backend.repository.ListItemRepository;
import com.lorecodex.backend.repository.UserListRepository;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.service.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserListServiceImpl implements UserListService {

    private final UserListRepository userListRepository;
    private final ListItemRepository listItemRepository;
    private final UserRepository userRepository;

    @Override
    public UserListResponse createList(Long userId, UserListRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserList userList = UserList.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();

        userList = userListRepository.save(userList);
        return toResponse(userList);
    }

    @Override
    public List<UserListResponse> getListsForUser(Long userId) {
        return userListRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserListResponse updateList(Long listId, UserListRequest request) {
        UserList list = getUserListByIdOrThrow(listId);

        list.setTitle(request.getTitle());
        list.setDescription(request.getDescription());

        return toResponse(userListRepository.save(list));
    }

    @Override
    public void deleteList(Long listId) {
        UserList list = getUserListByIdOrThrow(listId);
        userListRepository.delete(list);
    }

    @Override
    public void addItemToList(Long listId, ListItemRequest request) {
        UserList list = getUserListByIdOrThrow(listId);

        boolean alreadyExists = list.getItems().stream()
                .anyMatch(item -> item.getReferenceId().equals(request.getReferenceId())
                        && item.getType() == request.getType());

        if (alreadyExists) {
            throw new RuntimeException("Item already in the list");
        }

        ListItem item = ListItem.builder()
                .type(request.getType())
                .referenceId(request.getReferenceId())
                .position(request.getPosition())
                .userList(list)
                .build();

        listItemRepository.save(item);
    }

    @Override
    public void removeItemFromList(Long listId, Long itemId) {
        ListItem item = listItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUserList().getId().equals(listId)) {
            throw new RuntimeException("Item does not belong to the list");
        }

        listItemRepository.delete(item);
    }

    @Override
    public void reorderItems(Long listId, List<ReorderItemRequest> newOrder) {
        List<ListItem> items = listItemRepository.findByUserListIdOrderByPositionAsc(listId);

        for (ReorderItemRequest order : newOrder) {
            Optional<ListItem> itemOpt = items.stream()
                    .filter(i -> i.getId().equals(order.getItemId()))
                    .findFirst();

            itemOpt.ifPresent(item -> item.setPosition(order.getNewPosition()));
        }

        listItemRepository.saveAll(items);
    }

    private UserList getUserListByIdOrThrow(Long listId) {
        return userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));
    }

    private UserListResponse toResponse(UserList list) {
        return UserListResponse.builder()
                .id(list.getId())
                .title(list.getTitle())
                .description(list.getDescription())
                .createdAt(list.getCreatedAt())
                .userId(list.getUser().getId())
                .items(
                        list.getItems().stream()
                                .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
                                .map(this::toItemResponse)
                                .collect(Collectors.toList())
                )

                .build();
    }

    private ListItemResponse toItemResponse(ListItem item) {
        ListItemResponse res = new ListItemResponse();
        res.setId(item.getId());
        res.setReferenceId(item.getReferenceId());
        res.setType(item.getType());
        res.setPosition(item.getPosition());
        return res;
    }
}
