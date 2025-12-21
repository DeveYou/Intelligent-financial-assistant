package com.aitsaid.authservice.dtos;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
