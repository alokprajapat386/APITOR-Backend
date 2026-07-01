package org.example.apitor.common.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO (

    @NotBlank(message = "Username/Email is required")
     String identifier,
    @NotBlank(message = "Password is required")
     String password
){}
