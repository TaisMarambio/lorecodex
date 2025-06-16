package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.ListItemRequest;
import com.lorecodex.backend.dto.request.ReorderItemRequest;
import com.lorecodex.backend.dto.request.UserListRequest;
import com.lorecodex.backend.dto.response.UserListResponse;
import com.lorecodex.backend.mapper.UserListMapper;
import com.lorecodex.backend.model.ListItem;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserList;
import com.lorecodex.backend.repository.*;
import com.lorecodex.backend.service.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private final GameRepository gameRepository;
    private final GuideRepository guideRepository;
    private final ChallengeRepository challengeRepository;
    private final UserListMapper userListMapper;

    @Override
    public UserListResponse createList(Long userId, UserListRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserList userList = UserList.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .items(request.getItems().stream()
                        .map(itemRequest -> ListItem.builder()
                                .type(itemRequest.getType())
                                .referenceId(itemRequest.getReferenceId())
                                .position(itemRequest.getPosition())
                                .build())
                        .toList())
                .build();

        userList.setCreatedAt(LocalDateTime.now());
        userList.setUpdatedAt(LocalDateTime.now());
        userList = userListRepository.save(userList);
        return userListMapper.toResponse(userList);
    }

    @Override
    public List<UserListResponse> getListsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserList> lists = userListRepository.findByUserId(user.getId());
        return lists.stream()
                .map(userListMapper::toResponse)
                .toList();

    }

    @Transactional
    @Override
    public UserListResponse updateList(Long listId, UserListRequest request) {

        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        userList.setTitle(request.getTitle());
        userList.setDescription(request.getDescription());
        userList.setUpdatedAt(LocalDateTime.now());

        // ----- actualizar ítems -------
        if (request.getItems() != null) {

            // 1) vaciar la colección EXISTENTE
            userList.getItems().clear();

            // 2) mapear los DTO -> entidades
            List<ListItem> newItems = request.getItems().stream()
                    .map(dto -> {
                        ListItem li = ListItem.builder()
                                .type(dto.getType())
                                .referenceId(dto.getReferenceId())
                                .position(dto.getPosition())
                                .build();
                        li.setUserList(userList);       // ← clave para la FK
                        return li;
                    })
                    .toList();

            // 3) añadir uno a uno (misma instancia de List)
            userList.getItems().addAll(newItems);
        }

        // al tener cascade + orphanRemoval, sólo hace falta guardar ‘userList’
        return userListMapper.toResponse(userListRepository.save(userList));
    }


    // ---o bien---

        // ②  Si prefieres vaciar y rellenar la existente:
    /*
    List<ListItem> target = userList.getItems(); // colección gestionada
    target.clear();
    request.getItems().forEach(dto -> target.add(
        ListItem.builder()
            .type(dto.getType())
            .referenceId(dto.getReferenceId())
            .position(dto.getPosition())
            .build()
    ));
    */

    @Override
    public void deleteList(Long listId) {
        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        // Eliminar todos los items asociados a la lista
        listItemRepository.deleteAll(userList.getItems());

        // Eliminar la lista
        userListRepository.delete(userList);
    }

    @Override
    public void addItemToList(Long listId, ListItemRequest request) {
        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        ListItem item = ListItem.builder()
                .type(request.getType())
                .referenceId(request.getReferenceId())
                .position(request.getPosition())
                .userList(userList)
                .build();

        // Asignar la lista al item
        item.setUserList(userList);
        listItemRepository.save(item);

        // Actualizar la lista con el nuevo item
        userList.getItems().add(item);
        userListRepository.save(userList);
    }

    @Override
    public void removeItemFromList(Long listId, Long itemId) {
        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        ListItem item = listItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Verificar que el item pertenece a la lista
        if (!item.getUserList().getId().equals(listId)) {
            throw new RuntimeException("Item does not belong to the specified list");
        }

        // Eliminar el item de la lista y del repositorio
        userList.getItems().remove(item);
        listItemRepository.delete(item);
    }

    @Override
    public void reorderItems(Long listId, List<ReorderItemRequest> newOrder) {
        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        // Verificar que todos los items existen en la lista
        for (ReorderItemRequest request : newOrder) {
            Optional<ListItem> itemOpt = userList.getItems().stream()
                    .filter(item -> item.getId().equals(request.getItemId()))
                    .findFirst();
            if (itemOpt.isEmpty()) {
                throw new RuntimeException("Item with ID " + request.getItemId() + " not found in the list");
            }
        }

        // Reordenar los items
        for (int i = 0; i < newOrder.size(); i++) {
            final int index = i;
            ListItem item = userList.getItems().stream()
                    .filter(it -> it.getId().equals(newOrder.get(index).getItemId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Item not found in the list"));
            item.setPosition(i);
        }

        // Guardar los cambios
        listItemRepository.saveAll(userList.getItems());
    }

    @Override
    public UserListResponse getListById(Long listId) {
        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        return userListMapper.toResponse(userList);
    }

    @Override
    public List<UserListResponse> getAllLists() {
        List<UserList> allLists = userListRepository.findAll();
        return allLists.stream()
                .map(userListMapper::toResponse)
                .toList();
    }

    @Override
    public String getAuthorNameByListId(Long listId) {
        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        User user = userList.getUser();
        if (user != null) {
            return user.getUsername(); // Asumiendo que User tiene un campo username
        } else {
            throw new RuntimeException("User not found for the list");
        }
    }
}
