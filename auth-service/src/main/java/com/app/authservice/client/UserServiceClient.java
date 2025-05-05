package com.app.authservice.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.authservice.dto.RegisterRequest;
import com.app.authservice.dto.UserDto;

// call the fallback class if the feign client fails
@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {
    @GetMapping("/api/users/username/{username}")
    ResponseEntity<UserDto> getUserByUsername(@PathVariable String username);

    @GetMapping("/api/users/exists")
    ResponseEntity<Boolean> existsByUsername(@RequestParam String username);

//    @PutMapping("/api/users/{id}/last-login")
//    ResponseEntity<Void> updateLastLogin(@PathVariable String id, @RequestBody Map<String, LocalDateTime> lastLoginMap);

    @PostMapping("/api/users/register")
    ResponseEntity<UserDto> createUser(@RequestBody RegisterRequest registerRequest);

    @PostMapping("/api/users/login")
    ResponseEntity<UserDto> loginUser(@RequestBody Map<String, String> loginRequest);

    @GetMapping("/api/users/email/{email}")
    ResponseEntity<UserDto> getUserByEmail(@PathVariable String email);

}
