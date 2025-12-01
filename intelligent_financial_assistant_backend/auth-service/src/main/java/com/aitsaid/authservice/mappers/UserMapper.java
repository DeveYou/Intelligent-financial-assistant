package com.aitsaid.authservice.mappers;

import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.UpdateUserRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.Role;
import com.aitsaid.authservice.entities.User;

/**
 * @author radouane
 **/
public final class UserMapper {

    private UserMapper() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static User registerRequestToUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setCin(registerRequest.getCin());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setProfileImage(registerRequest.getProfileImage());
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        return user;
    }

    public static void updateUserFromRequest(UpdateUserRequest updateUserRequest, User user) {
        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setCin(updateUserRequest.getCin());
        user.setEmail(updateUserRequest.getEmail());
        user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        user.setAddress(updateUserRequest.getAddress());
        user.setProfileImage(updateUserRequest.getProfileImage());
    }

    public static UserDetails userToUserDetails(User user) {
        UserDetails userDetails = new UserDetails();
        userDetails.setId(user.getId());
        userDetails.setFirstName(user.getFirstName());
        userDetails.setLastName(user.getLastName());
        userDetails.setCin(user.getCin());
        userDetails.setEmail(user.getEmail());
        userDetails.setPhoneNumber(user.getPhoneNumber());
        userDetails.setAddress(user.getAddress());
        userDetails.setProfileImage(user.getProfileImage());
        userDetails.setCreatedAt(user.getCreatedAt());
        userDetails.setEnabled(user.isEnabled());

        return userDetails;
    }

}
