package org.example.apitor.common.controller;

import org.example.apitor.common.dto.PasswordChangeRequestDTO;
import org.example.apitor.common.dto.UserUpdateRequestDTO;
import org.example.apitor.common.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private  final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication auth){
        return ResponseEntity.ok(
            userService.getProfile(auth.getName())
        );
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateRequestDTO updateRequest, Authentication auth){

        return ResponseEntity.ok(
            userService.updateProfile(updateRequest, auth.getName())
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequestDTO passwordChangeRequest, Authentication auth){
        userService.changePassword(passwordChangeRequest, auth.getName());
        return ResponseEntity.ok().body(
                Map.of("message", "Password changed Successfully")
        );
    }


}
