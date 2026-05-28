package com.backend.gesteam.repository;

import com.backend.gesteam.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TrainingJpaRepository extends JpaRepository<Training, Long> {
    List<Training> findByTeamName(String teamName);
    List<Training> findByTeamNameAndDate(String teamName, String date);

    @Query("SELECT t FROM Training t WHERE t.isPublic = true")
    List<Training> findAllPublic();
}
