package org.example.apitor.common.dto;

public record ResetRequestVerificationDTO(
    String otp,
    String token,
    String newPassword
){}
