package com.lachguer.accountservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String cin;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDateTime createdAt;
    private boolean enabled;
}
