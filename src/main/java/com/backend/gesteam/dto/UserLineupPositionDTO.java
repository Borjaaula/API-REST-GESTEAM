package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLineupPositionDTO {

    @DecimalMin(value = "0.0", message = "La posición X debe ser al menos 0.0")
    @DecimalMax(value = "1.0", message = "La posición X debe ser como máximo 1.0")
    private Float x;


    @DecimalMin(value = "0.0", message = "La posición Y debe ser al menos 0.0")
    @DecimalMax(value = "1.0", message = "La posición Y debe ser como máximo 1.0")
    private Float y;
}
