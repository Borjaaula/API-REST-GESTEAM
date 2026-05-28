package com.backend.gesteam.dto;

public class AttendanceDTO {
    private Long id;
    private String playerName;
    private String status;  // "PRESENT", "ABSENT", "JUSTIFIED"

    public AttendanceDTO() {}

    public AttendanceDTO(Long id, String playerName, String status) {
        this.id = id;
        this.playerName = playerName;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
