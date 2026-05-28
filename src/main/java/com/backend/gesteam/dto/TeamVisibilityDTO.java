package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamVisibilityDTO {
    @NotNull(message = "La visibilidad debe ser especificada")
    private Boolean visible;
}

