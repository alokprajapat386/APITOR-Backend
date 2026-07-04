package org.example.apitor.common.service;

import org.example.apitor.common.dto.LoginResponseDTO;
import org.example.apitor.common.dto.PasswordChangeRequestDTO;
import org.example.apitor.common.dto.UserDetailsDTO;
import org.example.apitor.common.dto.UserUpdateRequestDTO;
import org.example.apitor.common.entity.User;
import org.example.apitor.common.repository.UserRepository;
import org.example.apitor.exceptions.ResourceNotFoundException;
import org.example.apitor.security.JwtService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository=userRepository;
        this.passwordEncoder= passwordEncoder;
        this.jwtService=jwtService;
    }

    public User getUser(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("User with username: " + username +" not found"));
    }

    public UserDetailsDTO getProfile(String username){
        return new UserDetailsDTO(getUser(username));
    }

    public LoginResponseDTO updateProfile(UserUpdateRequestDTO updateRequest, String username){

        User user=getUser(username);
        if(updateRequest.username()!=null && !updateRequest.username().equals(user.getUsername())){
            if(userRepository.existsByUsername(updateRequest.username())){
                throw new DuplicateKeyException("User with requested username: " + updateRequest.username() + " already exists");
            }

            if(updateRequest.username().contains("@")){
                throw new IllegalArgumentException("Username cannot consist '@");
            }
            user.setUsername(updateRequest.username());
        }
        if(updateRequest.fullName()!=null && !updateRequest.fullName().trim().isEmpty()) user.setFullName(updateRequest.fullName());
        if(updateRequest.email()!=null && !updateRequest.email().equals(user.getEmail())){
            if(userRepository.existsByEmail(updateRequest.email())){
                throw new DuplicateKeyException("User with requested email: " + updateRequest.email() + " already exists");
            }
            user.setEmail(updateRequest.email());
        }
        userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername());
        return new LoginResponseDTO(new UserDetailsDTO(user), token);
    }

    public void changePassword(PasswordChangeRequestDTO passwordResetRequest, String username){
        User user=userRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("User with username: " + username + " not found"));
        if(!passwordEncoder.matches(passwordResetRequest.oldPassword(), user.getPassword())){
            throw new BadCredentialsException("Password does not match, Invalid Password");
        }
        user.setPassword(passwordEncoder.encode(passwordResetRequest.newPassword()));
        userRepository.save(user);
    }
}
