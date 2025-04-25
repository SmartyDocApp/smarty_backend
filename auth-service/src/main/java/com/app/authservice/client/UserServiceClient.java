package com.app.authservice.client;

import com.app.authservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;

// call the fallback class if the feign client fails
@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {
    @GetMapping("/api/users/username/{username}")
    ResponseEntity<UserDto> getUserByUsername(@PathVariable String username);

    @PutMapping("/api/users/{id}/last-login")
    ResponseEntity<Void> updateLastLogin(@PathVariable String id, @RequestBody Map<String, LocalDateTime> lastLoginMap);
}
