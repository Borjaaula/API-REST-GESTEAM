package com.backend.gesteam.dto;

import com.backend.gesteam.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    @NotNull(message = "El tipo de usuario no puede ser nulo")
    private UserType type;

    private String teamName;
    private String clubName;
    private String status;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email es inválido")
    private String email;

    @NotNull(message = "La edad es obligatoria")
    @Positive(message = "La edad debe ser mayor que cero")
    private Integer age;

    @NotNull(message = "La altura es obligatoria")
    @Positive(message = "La altura debe ser mayor que cero")
    private Float height;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor que cero")
    private Float weight;

    private String phone;
}
