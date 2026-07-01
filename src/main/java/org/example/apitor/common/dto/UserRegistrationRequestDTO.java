package org.example.apitor.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDTO (
    @NotBlank(message = "Username is required")
    @Size(min = 3, max=30, message = "Username must be at least 3 characters and at most 30 characters long")
    String username,
    @NotBlank(message = "Name is required")
    String fullName,
    @NotBlank(message = "Email is required")
    String email,

    String password
){}
