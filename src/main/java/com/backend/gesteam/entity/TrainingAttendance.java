package com.backend.gesteam.entity;

import jakarta.persistence.*;

/**
 * Registro de asistencia de un jugador a un entrenamiento concreto.
 * El estado puede ser: "PRESENT" (presente), "ABSENT" (falta sin justificar)
 * o "JUSTIFIED" (falta justificada).
 * Cuando el estado cambia a/desde PRESENT, se actualiza el contador
 * {@code trainingAttendanceCount} del usuario correspondiente.
 */
@Entity
@Table(name = "training_attendance")
public class TrainingAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long trainingId;
    private String playerName;
    private String status;  // "PRESENT", "ABSENT", "JUSTIFIED"

    public TrainingAttendance() {}

    public TrainingAttendance(Long trainingId, String playerName, String status) {
        this.trainingId = trainingId;
        this.playerName = playerName;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainingId() { return trainingId; }
    public void setTrainingId(Long trainingId) { this.trainingId = trainingId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
