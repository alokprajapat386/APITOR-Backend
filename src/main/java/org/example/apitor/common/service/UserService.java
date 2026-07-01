package org.example.apitor.common.service;

import org.example.apitor.common.dto.PasswordChangeRequestDTO;
import org.example.apitor.common.dto.UserDetailsDTO;
import org.example.apitor.common.dto.UserUpdateRequestDTO;
import org.example.apitor.common.entity.User;
import org.example.apitor.common.repository.UserRepository;
import org.example.apitor.exceptions.ResourceNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder= passwordEncoder;
    }

    public User getUser(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("User with username: " + username +" not found"));
    }

    public UserDetailsDTO getProfile(String username){
        return new UserDetailsDTO(getUser(username));
    }

    public UserDetailsDTO updateProfile(UserUpdateRequestDTO updateRequest, String username){


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
        if(updateRequest.fullName()!=null) user.setFullName(updateRequest.fullName());
        if(updateRequest.email()!=null) user.setEmail(updateRequest.email());
        if(updateRequest.email()!=null && !updateRequest.email().equals(user.getEmail())){
            if(userRepository.existsByEmail(updateRequest.email())){
                throw new DuplicateKeyException("User with requested email: " + updateRequest.email() + " already exists");
            }
            user.setUsername(updateRequest.username());
        }
        userRepository.save(user);
        return new UserDetailsDTO(user);
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
