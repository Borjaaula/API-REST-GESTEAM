package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamCoachDTO {
    @NotBlank(message = "El nombre del entrenador no puede estar vacío")
    private String coachName;
}



