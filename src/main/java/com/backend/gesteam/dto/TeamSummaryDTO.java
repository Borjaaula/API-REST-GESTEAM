package com.backend.gesteam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamSummaryDTO {
    private String teamName;
    private String clubName;
    private String coachName;
    private int playerCount;
    private int totalGoals;
    private int totalAssists;
    private int totalMatches;
    private boolean lineupVisible;
}

