package org.example.apitor.common.dto;

public record UserUpdateRequestDTO (

        String username,

        String fullName,

        String email

){
}
