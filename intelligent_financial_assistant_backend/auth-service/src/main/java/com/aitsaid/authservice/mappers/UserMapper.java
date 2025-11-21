package com.aitsaid.authservice.mappers;

import com.aitsaid.authservice.dtos.RegisterRequest;
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
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        return user;
    }

}
