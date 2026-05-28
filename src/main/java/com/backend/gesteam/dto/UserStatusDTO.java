package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {
    @NotBlank(message = "El estado del usuario no puede estar vacío")
    private String status;
}

