package com.backend.gesteam.repository;

import com.backend.gesteam.entity.MatchEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface MatchEventJpaRepository extends JpaRepository<MatchEvent, Long> {
    List<MatchEvent> findByMatchIdOrderByMinuteAsc(Long matchId);

    @Modifying
    @Transactional
    void deleteByMatchId(Long matchId);
}
