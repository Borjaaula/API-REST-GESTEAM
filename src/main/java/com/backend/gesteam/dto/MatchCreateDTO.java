package com.backend.gesteam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchCreateDTO {
    private String teamName;
    private String rivalTeam;
    private String date;
    private String startTime;
    private int partDuration;
    private int breakDuration;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private String location;

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getRivalTeam() { return rivalTeam; }
    public void setRivalTeam(String rivalTeam) { this.rivalTeam = rivalTeam; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getPartDuration() { return partDuration; }
    public void setPartDuration(int partDuration) { this.partDuration = partDuration; }

    public int getBreakDuration() { return breakDuration; }
    public void setBreakDuration(int breakDuration) { this.breakDuration = breakDuration; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    private boolean manualHalftime;
    public boolean isManualHalftime() { return manualHalftime; }
    public void setManualHalftime(boolean manualHalftime) { this.manualHalftime = manualHalftime; }
}
