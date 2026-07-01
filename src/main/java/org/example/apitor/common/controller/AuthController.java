package org.example.apitor.common.controller;

import org.example.apitor.common.dto.*;
import org.example.apitor.common.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController{

    private final AuthService authService;
    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequestDTO registrationRequest){
   
        authService.register(registrationRequest);
        return ResponseEntity.ok(
                Map.of("message", "User registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDTO loginRequest){
        return ResponseEntity.ok(
                authService.login(loginRequest)
        );
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleOAuthRequestDTO googleLoginRequest) {
        return ResponseEntity.ok(
                 authService.handleOAuthGoogleLogin(googleLoginRequest)
        );
    }

    @PostMapping("/reset-password/get-token")
    public ResponseEntity<?> generatePasswordResetToken(@RequestBody ResetPasswordRequestDTO request) {
        return ResponseEntity.ok(
                authService.generateResetToken(request)
        );
    }

    @PostMapping("/reset-password/verify")
    public ResponseEntity<?> verifyPasswordResetRequest(@RequestBody ResetRequestVerificationDTO request){
        authService.verifyResetPasswordOtp(request);
        return ResponseEntity.ok(
                Map.of("message", "Password Reset Successfully")
        );
    }




}
