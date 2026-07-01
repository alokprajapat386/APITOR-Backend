package org.example.apitor.common.dto;

import org.example.apitor.common.entity.User;

import java.time.Instant;

public record UserDetailsDTO (
     Long id,
     String fullName,
     String username,
     String email,
     Instant registeredAt
){

    public UserDetailsDTO(User user) {
        this(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getRegisteredAt()
        );
    }
}
