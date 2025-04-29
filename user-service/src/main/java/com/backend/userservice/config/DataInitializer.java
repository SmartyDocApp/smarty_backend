
package com.backend.userservice.config;

import com.backend.userservice.model.Permission;
import com.backend.userservice.model.Role;
import com.backend.userservice.repository.PermissionRepository;
import com.backend.userservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    /*
        * Cette classe est utilisée pour initialiser les données de la base de données
        * Cela évite de duppliquer les rôles à chaque démarrage
        * Possibilité d'ajouter d'autres rôles et permissions par la suite
     */
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository,
                           PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void run(String... args) {
        // Créer les rôles de base s'ils n'existent pas
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrateur avec tous les droits");
            roleRepository.save(adminRole);
        }

        if (!roleRepository.existsByName("USER")) {
            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Utilisateur standard");
            roleRepository.save(userRole);
        }

    }
}