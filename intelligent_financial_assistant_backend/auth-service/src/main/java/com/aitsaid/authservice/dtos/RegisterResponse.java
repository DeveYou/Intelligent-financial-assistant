package com.aitsaid.authservice.dtos;

import lombok.Data;

/**
 * @author radouane
 **/
@Data
public class RegisterResponse {
    private String message;
    private String email;
    private String firstName;
    private String lastName;

    public RegisterResponse() {
    }

    public RegisterResponse(String email, String firstName, String lastName, String message) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.message = message;
    }
}
