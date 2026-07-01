package org.example.apitor.common.dto;

public record PasswordChangeRequestDTO (
    String oldPassword,
    String newPassword
){}
