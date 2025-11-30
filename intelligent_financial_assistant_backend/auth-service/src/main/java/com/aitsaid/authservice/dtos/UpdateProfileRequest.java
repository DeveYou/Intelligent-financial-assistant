package com.aitsaid.authservice.dtos;

import lombok.Data;

/**
 * @author radouane
 **/
@Data
public class UpdateProfileRequest {

    private String cin;
    private String address;
    private String phoneNumber;
}
