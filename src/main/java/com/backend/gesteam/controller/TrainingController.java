package com.backend.gesteam.controller;

import com.backend.gesteam.dto.AttendanceDTO;
import com.backend.gesteam.dto.TrainingCreateDTO;
import com.backend.gesteam.dto.TrainingResponseDTO;
import com.backend.gesteam.entity.Training;
import com.backend.gesteam.service.training.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trainings")
@CrossOrigin(origins = "*")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    @PostMapping
    public ResponseEntity<TrainingResponseDTO> createTraining(@RequestBody TrainingCreateDTO dto) {
        Training training = new Training(
                dto.getTeamName(), dto.getDate(), dto.getTime(),
                dto.getNotes(), dto.isPublic(), dto.getCreatedBy()
        );
        Training saved = trainingService.createTraining(training);
        return ResponseEntity.status(201).body(trainingService.toDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingResponseDTO> getById(@PathVariable Long id) {
        return trainingService.getById(id)
                .map(t -> ResponseEntity.ok(trainingService.toDTO(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/team/{teamName}")
    public ResponseEntity<List<TrainingResponseDTO>> getByTeam(@PathVariable String teamName) {
        List<TrainingResponseDTO> list = trainingService.getByTeam(teamName).stream()
                .map(trainingService::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/team/{teamName}/date/{date}")
    public ResponseEntity<List<TrainingResponseDTO>> getByTeamAndDate(
            @PathVariable String teamName, @PathVariable String date) {
        List<TrainingResponseDTO> list = trainingService.getByTeamAndDate(teamName, date).stream()
                .map(trainingService::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingResponseDTO> updateTraining(
            @PathVariable Long id, @RequestBody TrainingCreateDTO dto) {
        return trainingService.getById(id).map(training -> {
            training.setDate(dto.getDate());
            training.setTime(dto.getTime());
            training.setNotes(dto.getNotes());
            training.setPublic(dto.isPublic());
            return ResponseEntity.ok(trainingService.toDTO(trainingService.updateTraining(training)));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<Void> updateVisibility(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        trainingService.updateVisibility(id, Boolean.TRUE.equals(body.get("isPublic")));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<AttendanceDTO>> getAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(trainingService.getAttendance(id));
    }

    @PostMapping("/{id}/attendance")
    public ResponseEntity<Void> updateAttendance(@PathVariable Long id, @RequestBody AttendanceDTO dto) {
        trainingService.upsertAttendance(id, dto.getPlayerName(), dto.getStatus());
        return ResponseEntity.noContent().build();
    }
}
