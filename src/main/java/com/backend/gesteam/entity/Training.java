package com.backend.gesteam.entity;

import jakarta.persistence.*;

/**
 * Entidad que representa un entrenamiento de equipo.
 * La asistencia de cada jugador se gestiona en la tabla separada {@link TrainingAttendance}.
 */
@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;
    private String date;       // ISO: "2026-05-22"
    private String time;       // "10:30"

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_public")
    private boolean isPublic;
    private String createdBy;  // coach username

    public Training() {}

    public Training(String teamName, String date, String time, String notes, boolean isPublic, String createdBy) {
        this.teamName = teamName;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.isPublic = isPublic;
        this.createdBy = createdBy;
    }

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
}
