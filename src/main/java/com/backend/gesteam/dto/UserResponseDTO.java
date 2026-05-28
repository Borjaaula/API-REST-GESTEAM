package com.backend.gesteam.dto;

import com.backend.gesteam.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String name;
    private UserType type;
    private String teamName;
    private String clubName;
    private String status;
    private String email;
    private int age;
    private float height;
    private float weight;
    private String profileImageUri;
    private int matchesPlayed;
    private int matchesStarted;
    private int goals;
    private int assists;
    private String phone;
    private float lineupX;
    private float lineupY;
    private int trainingAttendanceCount;
    private String position;
    private String secondaryPosition;
}
