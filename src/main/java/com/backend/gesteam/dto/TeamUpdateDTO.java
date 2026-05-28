package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateDTO {
    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    private String name;
    @NotBlank(message = "El nombre del club no puede estar vacío")
    private String clubName;
    @NotNull(message = "La visibilidad de la alineación no puede estar vacía")
    private Boolean lineupVisible;
    private String profileImageUri;
}

