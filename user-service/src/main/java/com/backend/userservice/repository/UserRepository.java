package com.backend.userservice.repository;

import com.backend.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);
    boolean existsByEmail(String email);


}
