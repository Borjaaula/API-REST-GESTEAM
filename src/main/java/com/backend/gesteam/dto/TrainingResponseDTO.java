package com.backend.gesteam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TrainingResponseDTO {
    private Long id;
    private String teamName;
    private String date;
    private String time;
    private String notes;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private String createdBy;
    private List<AttendanceDTO> attendance;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public List<AttendanceDTO> getAttendance() { return attendance; }
    public void setAttendance(List<AttendanceDTO> attendance) { this.attendance = attendance; }
}
