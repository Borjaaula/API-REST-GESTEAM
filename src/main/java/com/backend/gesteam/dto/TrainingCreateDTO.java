package com.backend.gesteam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingCreateDTO {
    private String teamName;
    private String date;
    private String time;
    private String notes;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private String createdBy;

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
}
