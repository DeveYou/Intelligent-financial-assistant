package com.aitsaid.authservice.dtos;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author radouane
 **/
@Data
public class UserDetails {

    private String firstName;
    private String lastName;
    private String cin;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdAt;
    private boolean enabled;
}
