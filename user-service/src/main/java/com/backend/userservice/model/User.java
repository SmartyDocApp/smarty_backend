package com.backend.userservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    //sert à gérer l'état actif/inactif d'un compte utilisateur
    private boolean enabled = true;


    @ManyToMany(fetch = FetchType.EAGER) // EAGER spécifie que les rôles seront chargés avec l'utilisateur
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"), // défini la clé étrangère pour l'utilisateur
            inverseJoinColumns = @JoinColumn(name = "role_id") //  Définit la clé étrangère qui relie la table user_roles à la table roles via la colonne role_id.
    )

    private Set<Role> roles = new HashSet<>();


}



