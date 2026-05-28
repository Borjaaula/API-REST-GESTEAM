package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    @NotNull(message = "El número de partidos jugados es obligatorio")
    @Min(value = 0, message = "Los partidos jugados no pueden ser negativos")
    private Integer matchesPlayed;

    @NotNull(message = "El número de partidos como titular es obligatorio")
    @Min(value = 0, message = "Los partidos como titular no pueden ser negativos")
    private Integer matchesStarted;

    @NotNull(message = "La cantidad de goles es obligatoria")
    @Min(value = 0, message = "Los goles no pueden ser negativos")
    private Integer goals;

    @NotNull(message = "La cantidad de asistencias es obligatoria")
    @Min(value = 0, message = "Las asistencias no pueden ser negativas")
    private Integer assists;
}

