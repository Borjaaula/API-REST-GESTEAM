package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamDTO {
    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    private String teamName;
}

