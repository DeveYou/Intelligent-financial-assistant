package com.aitsaid.authservice.dtos;

import lombok.Data;

/**
 * @author radouane
 **/
@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String cin;
    private String address;
    private String phoneNumber;
    private Boolean enabled;
}
