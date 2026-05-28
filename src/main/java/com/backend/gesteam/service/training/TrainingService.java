package com.backend.gesteam.service.training;

import com.backend.gesteam.dto.AttendanceDTO;
import com.backend.gesteam.dto.TrainingResponseDTO;
import com.backend.gesteam.entity.Training;
import com.backend.gesteam.entity.TrainingAttendance;
import com.backend.gesteam.repository.TrainingAttendanceJpaRepository;
import com.backend.gesteam.repository.TrainingJpaRepository;
import com.backend.gesteam.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainingService {

    @Autowired
    private TrainingJpaRepository trainingRepo;

    @Autowired
    private TrainingAttendanceJpaRepository attendanceRepo;

    @Autowired
    private UserJpaRepository userRepo;

    public Training createTraining(Training training) {
        return trainingRepo.save(training);
    }

    public Optional<Training> getById(Long id) {
        return trainingRepo.findById(id);
    }

    public List<Training> getByTeam(String teamName) {
        return trainingRepo.findByTeamName(teamName);
    }

    public List<Training> getByTeamAndDate(String teamName, String date) {
        return trainingRepo.findByTeamNameAndDate(teamName, date);
    }

    public Training updateTraining(Training training) {
        return trainingRepo.save(training);
    }

    public void deleteTraining(Long id) {
        // Antes de borrar, decrementar el contador de los jugadores que asistieron
        attendanceRepo.findByTrainingId(id).stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()))
                .forEach(a -> userRepo.findByName(a.getPlayerName()).ifPresent(u -> {
                    u.setTrainingAttendanceCount(Math.max(0, u.getTrainingAttendanceCount() - 1));
                    userRepo.save(u);
                }));
        attendanceRepo.deleteByTrainingId(id);
        trainingRepo.deleteById(id);
    }

    public void updateVisibility(Long id, boolean isPublic) {
        trainingRepo.findById(id).ifPresent(t -> {
            t.setPublic(isPublic);
            trainingRepo.save(t);
        });
    }

    public List<AttendanceDTO> getAttendance(Long trainingId) {
        return attendanceRepo.findByTrainingId(trainingId).stream()
                .map(a -> new AttendanceDTO(a.getId(), a.getPlayerName(), a.getStatus()))
                .collect(Collectors.toList());
    }

    public void upsertAttendance(Long trainingId, String playerName, String status) {
        Optional<TrainingAttendance> existing = attendanceRepo.findByTrainingIdAndPlayerName(trainingId, playerName);
        String previousStatus = existing.map(TrainingAttendance::getStatus).orElse(null);
        TrainingAttendance att = existing.orElse(new TrainingAttendance(trainingId, playerName, status));
        att.setStatus(status);
        attendanceRepo.save(att);

        // Actualizar contador de asistencias del jugador
        userRepo.findByName(playerName).ifPresent(user -> {
            boolean wasPresent = "PRESENT".equalsIgnoreCase(previousStatus);
            boolean nowPresent = "PRESENT".equalsIgnoreCase(status);
            if (!wasPresent && nowPresent) {
                user.setTrainingAttendanceCount(user.getTrainingAttendanceCount() + 1);
                userRepo.save(user);
            } else if (wasPresent && !nowPresent) {
                user.setTrainingAttendanceCount(Math.max(0, user.getTrainingAttendanceCount() - 1));
                userRepo.save(user);
            }
        });
    }

    public TrainingResponseDTO toDTO(Training t) {
        TrainingResponseDTO dto = new TrainingResponseDTO();
        dto.setId(t.getId());
        dto.setTeamName(t.getTeamName());
        dto.setDate(t.getDate());
        dto.setTime(t.getTime());
        dto.setNotes(t.getNotes());
        dto.setPublic(t.isPublic());
        dto.setCreatedBy(t.getCreatedBy());
        dto.setAttendance(getAttendance(t.getId()));
        return dto;
    }
}
