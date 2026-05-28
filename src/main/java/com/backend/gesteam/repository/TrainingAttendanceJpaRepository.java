package com.backend.gesteam.repository;

import com.backend.gesteam.entity.TrainingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface TrainingAttendanceJpaRepository extends JpaRepository<TrainingAttendance, Long> {
    List<TrainingAttendance> findByTrainingId(Long trainingId);
    Optional<TrainingAttendance> findByTrainingIdAndPlayerName(Long trainingId, String playerName);

    @Modifying
    @Transactional
    void deleteByTrainingId(Long trainingId);
}
