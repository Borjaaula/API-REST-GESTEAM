package com.backend.gesteam.dto;

import com.backend.gesteam.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO {
    @NotNull(message = "El tipo de usuario no puede ser nulo")
    private UserType type;
    @NotBlank(message = "El nombre del club no puede estar vacío")
    private String clubName;
}

