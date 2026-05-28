package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateDTO {
    private String email;
    private Integer age;
    private Float height;
    private Float weight;
    private String profileImageUri;
    private String phone;
    private String position;
    private String secondaryPosition;
}
