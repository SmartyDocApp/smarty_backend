package com.backend.userservice.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER spécifie que les rôles seront chargés avec l'utilisateur
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "role_id"),// défini la clé étrangère pour le rôle
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )

    private Set<Permission> permissions = new HashSet<>();


}
