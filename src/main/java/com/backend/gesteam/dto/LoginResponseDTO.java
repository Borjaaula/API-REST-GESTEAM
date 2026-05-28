package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String tokenType;
    private String token;
    private UserResponseDTO user;
    private String errorMessage;
}

