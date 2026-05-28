package com.backend.gesteam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MatchResponseDTO {
    private Long id;
    private String teamName;
    private String rivalTeam;
    private String date;
    private String startTime;
    private int partDuration;
    private int breakDuration;
    private int goalsFor;
    private int goalsAgainst;
    private String status;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private String location;
    private String clubName;
    private Long liveStartedAt;
    private List<MatchEventResponseDTO> events;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public int getGoalsFor() { return goalsFor; }
    public void setGoalsFor(int goalsFor) { this.goalsFor = goalsFor; }

    public int getGoalsAgainst() { return goalsAgainst; }
    public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public Long getLiveStartedAt() { return liveStartedAt; }
    public void setLiveStartedAt(Long liveStartedAt) { this.liveStartedAt = liveStartedAt; }

    private Long halftimeEndedAt;
    public Long getHalftimeEndedAt() { return halftimeEndedAt; }
    public void setHalftimeEndedAt(Long halftimeEndedAt) { this.halftimeEndedAt = halftimeEndedAt; }

    public List<MatchEventResponseDTO> getEvents() { return events; }
    public void setEvents(List<MatchEventResponseDTO> events) { this.events = events; }

    private boolean manualHalftime;
    public boolean isManualHalftime() { return manualHalftime; }
    public void setManualHalftime(boolean manualHalftime) { this.manualHalftime = manualHalftime; }
}
