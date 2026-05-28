package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    @NotBlank(message = "La contraseña actual no puede estar vacía")
    private String oldPassword;
    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    private String newPassword;
}



