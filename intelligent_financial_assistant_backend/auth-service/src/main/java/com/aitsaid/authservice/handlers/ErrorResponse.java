package com.aitsaid.authservice.handlers;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author radouane
 **/
@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
}