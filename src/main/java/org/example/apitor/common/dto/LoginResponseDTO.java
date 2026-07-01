package org.example.apitor.common.dto;

public record LoginResponseDTO (
        UserDetailsDTO userDetails,
        String token
){}
