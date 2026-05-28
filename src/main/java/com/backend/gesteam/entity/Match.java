package com.backend.gesteam.entity;

import jakarta.persistence.*;

/**
 * Entidad que representa un partido.
 * El ciclo de vida del estado es: SCHEDULED → LIVE → HALFTIME → LIVE → FINISHED.
 * {@code liveStartedAt} y {@code halftimeEndedAt} son timestamps epoch (ms) usados
 * para calcular el cronómetro del partido en el cliente sin peticiones adicionales.
 * Si {@code manualHalftime} es true, el entrenador controla manualmente el paso
 * de primera a segunda parte; de lo contrario se calcula por tiempo transcurrido.
 */
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;
    private String rivalTeam;
    private String date;         // ISO: "2026-05-22"
    private String startTime;    // "16:00"
    private int partDuration;    // minutes per half (e.g. 45)
    private int breakDuration;   // break minutes (e.g. 15)
    private int goalsFor;
    private int goalsAgainst;
    private String status;       // "SCHEDULED", "LIVE", "FINISHED"
    @Column(name = "is_public")
    private boolean isPublic;
    private String location;
    private Long liveStartedAt;      // epoch ms cuando el partido pasó a LIVE (1ª parte)
    private Long halftimeEndedAt;    // epoch ms cuando el entrenador inicia la 2ª parte (control manual)
    private boolean manualHalftime;  // true = el entrenador controla el partido manualmente

    public Match() {
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.status = "SCHEDULED";
        this.partDuration = 45;
        this.breakDuration = 15;
    }

    public Match(String teamName, String rivalTeam, String date, String startTime,
                 int partDuration, int breakDuration, boolean isPublic) {
        this();
        this.teamName = teamName;
        this.rivalTeam = rivalTeam;
        this.date = date;
        this.startTime = startTime;
        this.partDuration = partDuration;
        this.breakDuration = breakDuration;
        this.isPublic = isPublic;
    }

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

    public Long getLiveStartedAt() { return liveStartedAt; }
    public void setLiveStartedAt(Long liveStartedAt) { this.liveStartedAt = liveStartedAt; }

    public Long getHalftimeEndedAt() { return halftimeEndedAt; }
    public void setHalftimeEndedAt(Long halftimeEndedAt) { this.halftimeEndedAt = halftimeEndedAt; }

    public boolean isManualHalftime() { return manualHalftime; }
    public void setManualHalftime(boolean manualHalftime) { this.manualHalftime = manualHalftime; }
}
