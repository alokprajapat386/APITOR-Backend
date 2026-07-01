package org.example.apitor.common.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.example.apitor.common.dto.*;
import org.example.apitor.common.entity.User;
import org.example.apitor.common.enums.AuthProvider;
import org.example.apitor.common.repository.UserRepository;
import org.example.apitor.exceptions.ResourceNotFoundException;
import org.example.apitor.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;

@Service
public class AuthService {

    @Value("google.client.id")
    private String googleClientId;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TotpUtil totpUtil;
    private final EmailService emailService;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TotpUtil totpUtil, EmailService emailService){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
        this.totpUtil=totpUtil;
        this.emailService=emailService;
    }

    public void register(UserRegistrationRequestDTO registrationRequest){

        if(userRepository.existsByEmail(registrationRequest.email())){
            throw new DuplicateKeyException("User with email: " + registrationRequest.email() + " already exists");
        }

        if(userRepository.existsByUsername(registrationRequest.username())){
            throw new DuplicateKeyException("User with username: " + registrationRequest.username() + " already exists");
        }
        if(registrationRequest.username().contains("@")){
            throw new IllegalArgumentException("Username cannot consist '@'");
        }
        User user = new User();
        user.setUsername(registrationRequest.username());
        user.setFullName(registrationRequest.fullName());
        user.setEmail(registrationRequest.email());
        user.setPassword(passwordEncoder.encode(registrationRequest.password()));
        user.setRegisteredAt(Instant.now());
        user.setAuthProvider(AuthProvider.LOCAL);
        userRepository.save(user);
    }

    public LoginResponseDTO login(UserLoginRequestDTO loginRequest){
        User user = userRepository.findByUsernameOrEmail(loginRequest.identifier(), loginRequest.identifier())
                .orElseThrow(()-> new ResourceNotFoundException("User with username/email: " + loginRequest.identifier() + " found"));
        UserDetailsDTO userDetails = new UserDetailsDTO(user);
        String token = jwtService.generateToken(user.getUsername());
        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())){
            throw new BadCredentialsException("Invalid Password");
        }
        return new LoginResponseDTO(userDetails, token);
    }

    public LoginResponseDTO oauthGoogle(UserRegistrationRequestDTO registrationRequest){
        // user already exists, login
        if(userRepository.existsByEmail(registrationRequest.email())){
            User user = userRepository.findByEmail(registrationRequest.email())
                    .orElseThrow(()-> new ResourceNotFoundException("User with email: " + registrationRequest.email() + " not found"));
            UserDetailsDTO userDetails = new UserDetailsDTO(user);
            String token = jwtService.generateToken(user.getUsername());
            return new LoginResponseDTO(userDetails, token);
        }
        // user doesn't exist register and login
        else{
            User user = new User();
            user.setUsername(registrationRequest.username());
            user.setFullName(registrationRequest.fullName());
            user.setEmail(registrationRequest.email());
            user.setRegisteredAt(Instant.now());
            user.setAuthProvider(AuthProvider.GOOGLE);
            userRepository.save(user);

            UserDetailsDTO userDetails = new UserDetailsDTO(user);
            String token = jwtService.generateToken(user.getUsername());
            return new LoginResponseDTO(userDetails, token);

        }
    }

    public LoginResponseDTO handleOAuthGoogleLogin(GoogleOAuthRequestDTO googleLoginRequest)  {
        try{
            NetHttpTransport transport = new NetHttpTransport();
            GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleLoginRequest.tokenId());

            if(idToken!=null){
                GoogleIdToken.Payload payload = idToken.getPayload();

                String googleUserId = payload.getSubject();
                String email = payload.getEmail();
                String name= (String) payload.get("name");
                UserRegistrationRequestDTO registrationRequest= new UserRegistrationRequestDTO(
                        googleUserId,
                        name,
                        email,
                        null
                );

                return oauthGoogle(registrationRequest);
            }else{
                throw new BadCredentialsException("Invalid IdToken");
            }

        }catch(GeneralSecurityException | IOException e){
            throw new AuthenticationServiceException("Failed to perform authentication with Google Identity Provider");
        }
    }

    public PasswordResetTokenResponseDTO generateResetToken(ResetPasswordRequestDTO resetPasswordRequest) {
        User user = userRepository.findByUsernameOrEmail(resetPasswordRequest.identifier(), resetPasswordRequest.identifier())
                .orElseThrow(()-> new ResourceNotFoundException("User with username/email: " + resetPasswordRequest.identifier() + " not found"));
        long expiryTime = Instant.now().getEpochSecond() + 300;
        String generatedOtp=totpUtil.generateOtp(user.getEmail(), expiryTime);
        String rawPayload = user.getEmail()+":"+expiryTime;
        String resetToken = Base64.getEncoder().encodeToString(rawPayload.getBytes());
        try{
            emailService.sendPasswordResetToken(user.getUsername(), user.getEmail(), generatedOtp);
        } catch (MessagingException e){
            throw new ProviderException("Failed to dispatch OTP via Email Provider");
        }

        return new PasswordResetTokenResponseDTO(
          resetToken
        );
    }

    public void verifyResetPasswordOtp(ResetRequestVerificationDTO verificationRequest){
        String email = totpUtil.verifyOtpAndExtractEmail(verificationRequest.otp(), verificationRequest.token());
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User with email : " + email + " not found"));
        user.setPassword(passwordEncoder.encode(verificationRequest.newPassword()));
        userRepository.save(user);
    }
}
