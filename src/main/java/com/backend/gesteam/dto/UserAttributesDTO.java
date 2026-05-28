package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAttributesDTO {
    @NotNull(message = "La edad es obligatoria")
    @Positive(message = "La edad debe ser mayor que cero")
    private Integer age;
    @NotNull(message = "La altura es obligatoria")
    @Positive(message = "La altura debe ser mayor que cero")
    private Float height;
    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor que cero")
    private Float weight;
}

