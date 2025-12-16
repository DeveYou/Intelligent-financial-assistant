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
    private Long id;
    private String address;
    private String phoneNumber;
    private String cin;

    public LoginResponse() {
    }

    public LoginResponse(String token, String email, String firstName, String lastName, Long id, String address, String phoneNumber, String cin, String message) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.cin = cin;
        this.message = message;
        this.type = "Bearer";

    }
}
