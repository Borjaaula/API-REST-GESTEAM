package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDTO {
    private Long id;
    private String name;
    private String clubName;
    private UserResponseDTO coach;
    private List<UserResponseDTO> coaches;
    private List<UserResponseDTO> players;
    private boolean isLineupVisible;
    private String profileImageUri;
}

