package com.aitsaid.authservice.dtos;

import lombok.Data;

/**
 * @author radouane
 **/
@Data
public class LoginResponse {
    private String message;
    private String token;
    private String type;
    private String email;
    private String firstName;
    private String lastName;

    public LoginResponse() {
    }

    public LoginResponse(String token, String email, String firstName, String lastName, String message) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.message = message;
        this.type = "Bearer";

    }
}
