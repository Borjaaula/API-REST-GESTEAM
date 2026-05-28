package com.backend.gesteam.service.match;

import com.backend.gesteam.dto.MatchEventResponseDTO;
import com.backend.gesteam.dto.MatchResponseDTO;
import com.backend.gesteam.entity.Match;
import com.backend.gesteam.entity.MatchEvent;
import com.backend.gesteam.repository.MatchEventJpaRepository;
import com.backend.gesteam.repository.MatchJpaRepository;
import com.backend.gesteam.repository.TeamJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchService {

    @Autowired
    private MatchJpaRepository matchRepo;

    @Autowired
    private MatchEventJpaRepository eventRepo;

    @Autowired
    private TeamJpaRepository teamRepo;

    public Match createMatch(Match match) {
        return matchRepo.save(match);
    }

    public Optional<Match> getById(Long id) {
        return matchRepo.findById(id);
    }

    public List<Match> getByTeam(String teamName) {
        return matchRepo.findByTeamName(teamName);
    }

    public List<Match> getByTeamAndDate(String teamName, String date) {
        return matchRepo.findByTeamNameAndDate(teamName, date);
    }

    public List<Match> getAllPublic() {
        return matchRepo.findAllPublic();
    }

    public List<Match> searchPublicByTeam(String teamFilter) {
        if (teamFilter == null || teamFilter.isBlank()) return getAllPublic();
        return matchRepo.findPublicByTeamContaining(teamFilter);
    }

    /** Devuelve TODOS los partidos (públicos y privados) de todos los equipos de un club. */
    public List<Match> getMatchesByClub(String clubName) {
        List<String> teamNames = teamRepo.findByClubName(clubName).stream()
                .map(team -> team.getName())
                .collect(Collectors.toList());
        if (teamNames.isEmpty()) return Collections.emptyList();
        return matchRepo.findByTeamNameIn(teamNames);
    }

    public Match updateMatch(Match match) {
        return matchRepo.save(match);
    }

    public void deleteMatch(Long id) {
        eventRepo.deleteByMatchId(id);
        matchRepo.deleteById(id);
    }

    public void updateStatus(Long id, String status) {
        matchRepo.findById(id).ifPresent(m -> {
            m.setStatus(status);
            matchRepo.save(m);
        });
    }

    public MatchEvent addEvent(MatchEvent event) {
        MatchEvent saved = eventRepo.save(event);
        // Recalcular marcador si el evento es un gol
        if ("GOAL".equals(event.getType())) {
            matchRepo.findById(event.getMatchId()).ifPresent(m -> {
                long goals = eventRepo.findByMatchIdOrderByMinuteAsc(m.getId())
                        .stream().filter(e -> "GOAL".equals(e.getType())).count();
                m.setGoalsFor((int) goals);
                matchRepo.save(m);
            });
        }
        return saved;
    }

    public void deleteEvent(Long eventId) {
        eventRepo.findById(eventId).ifPresent(ev -> {
            eventRepo.delete(ev);
            // Recalcular marcador
            if ("GOAL".equals(ev.getType())) {
                matchRepo.findById(ev.getMatchId()).ifPresent(m -> {
                    long goals = eventRepo.findByMatchIdOrderByMinuteAsc(m.getId())
                            .stream().filter(e -> "GOAL".equals(e.getType())).count();
                    m.setGoalsFor((int) goals);
                    matchRepo.save(m);
                });
            }
        });
    }

    public List<MatchEvent> getEvents(Long matchId) {
        return eventRepo.findByMatchIdOrderByMinuteAsc(matchId);
    }

    public List<MatchEventResponseDTO> getEventDTOs(Long matchId) {
        return getEvents(matchId).stream().map(this::eventToDTO).collect(Collectors.toList());
    }

    public MatchResponseDTO toDTO(Match m) {
        MatchResponseDTO dto = new MatchResponseDTO();
        dto.setId(m.getId());
        dto.setTeamName(m.getTeamName());
        dto.setRivalTeam(m.getRivalTeam());
        dto.setDate(m.getDate());
        dto.setStartTime(m.getStartTime());
        dto.setPartDuration(m.getPartDuration());
        dto.setBreakDuration(m.getBreakDuration());
        dto.setGoalsFor(m.getGoalsFor());
        dto.setGoalsAgainst(m.getGoalsAgainst());
        dto.setStatus(m.getStatus());
        dto.setPublic(m.isPublic());
        dto.setLocation(m.getLocation());
        dto.setLiveStartedAt(m.getLiveStartedAt());
        dto.setHalftimeEndedAt(m.getHalftimeEndedAt());
        dto.setManualHalftime(m.isManualHalftime());
        dto.setEvents(getEventDTOs(m.getId()));
        if (m.getTeamName() != null) {
            teamRepo.findByName(m.getTeamName()).ifPresent(t -> dto.setClubName(t.getClubName()));
        }
        return dto;
    }

    private MatchEventResponseDTO eventToDTO(MatchEvent e) {
        MatchEventResponseDTO dto = new MatchEventResponseDTO();
        dto.setId(e.getId());
        dto.setMatchId(e.getMatchId());
        dto.setType(e.getType());
        dto.setPlayerName(e.getPlayerName());
        dto.setMinute(e.getMinute());
        dto.setSecond(e.getSecond());
        dto.setAssistPlayerName(e.getAssistPlayerName());
        dto.setPlayerOutName(e.getPlayerOutName());
        dto.setPlayerInName(e.getPlayerInName());
        dto.setHalf(e.getHalf() > 0 ? e.getHalf() : 1);
        return dto;
    }
}
