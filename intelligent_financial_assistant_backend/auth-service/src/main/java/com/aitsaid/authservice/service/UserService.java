package com.aitsaid.authservice.service;

import com.aitsaid.authservice.dtos.UpdateProfileRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.Role;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.exceptions.UserNotFoundException;
import com.aitsaid.authservice.mappers.UserMapper;
import com.aitsaid.authservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author radouane
 **/
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDetails> getAllUsers() {
        List<User> users = userRepository.findAllByRole(Role.ROLE_USER);

        return users.stream()
                .map(UserMapper::userToUserDetails)
                .collect(Collectors.toList());
    }

    public User updateUserProfile(Long userId, UpdateProfileRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        if (updateRequest.getCin() != null) {
            user.setCin(updateRequest.getCin());
        }
        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }
        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        return userRepository.save(user);
    }

}
