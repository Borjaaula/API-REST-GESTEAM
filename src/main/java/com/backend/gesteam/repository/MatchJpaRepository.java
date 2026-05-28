package com.backend.gesteam.repository;

import com.backend.gesteam.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MatchJpaRepository extends JpaRepository<Match, Long> {
    List<Match> findByTeamName(String teamName);
    List<Match> findByTeamNameAndDate(String teamName, String date);
    List<Match> findByStatus(String status);

    @Query("SELECT m FROM Match m WHERE m.isPublic = true")
    List<Match> findAllPublic();

    @Query("SELECT m FROM Match m WHERE m.isPublic = true AND LOWER(m.teamName) LIKE LOWER(CONCAT('%', :team, '%'))")
    List<Match> findPublicByTeamContaining(@Param("team") String team);

    @Query("SELECT m FROM Match m WHERE m.teamName IN :teamNames ORDER BY m.date DESC")
    List<Match> findByTeamNameIn(@Param("teamNames") List<String> teamNames);
}
