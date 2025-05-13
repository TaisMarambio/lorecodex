package com.lorecodex.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "list_items")
public class ListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ListItemType type; // GAME, GUIDE, CHALLENGE

    private Long referenceId; // Id del juego, guía o challenge

    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_list_id") // fk en la tabla
    private UserList userList;
}