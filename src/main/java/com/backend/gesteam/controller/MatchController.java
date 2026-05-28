package com.backend.gesteam.controller;

import com.backend.gesteam.config.LineupWebSocketHandler;
import com.backend.gesteam.dto.MatchCreateDTO;
import com.backend.gesteam.dto.MatchEventCreateDTO;
import com.backend.gesteam.dto.MatchEventResponseDTO;
import com.backend.gesteam.dto.MatchResponseDTO;
import com.backend.gesteam.entity.Match;
import com.backend.gesteam.entity.MatchEvent;
import com.backend.gesteam.repository.TeamJpaRepository;
import com.backend.gesteam.repository.UserJpaRepository;
import com.backend.gesteam.service.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private LineupWebSocketHandler lineupWsHandler;

    @Autowired
    private UserJpaRepository userRepo;

    @Autowired
    private TeamJpaRepository teamRepo;

    @PostMapping
    public ResponseEntity<MatchResponseDTO> createMatch(@RequestBody MatchCreateDTO dto) {
        Match match = new Match(dto.getTeamName(), dto.getRivalTeam(), dto.getDate(),
                dto.getStartTime(), dto.getPartDuration(), dto.getBreakDuration(), dto.isPublic());
        match.setLocation(dto.getLocation());
        match.setManualHalftime(dto.isManualHalftime());
        Match saved = matchService.createMatch(match);
        return ResponseEntity.status(201).body(matchService.toDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDTO> getById(@PathVariable Long id) {
        return matchService.getById(id)
                .map(m -> ResponseEntity.ok(matchService.toDTO(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/team/{teamName}")
    public ResponseEntity<List<MatchResponseDTO>> getByTeam(@PathVariable String teamName) {
        List<MatchResponseDTO> list = matchService.getByTeam(teamName).stream()
                .map(matchService::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/team/{teamName}/date/{date}")
    public ResponseEntity<List<MatchResponseDTO>> getByTeamAndDate(
            @PathVariable String teamName, @PathVariable String date) {
        List<MatchResponseDTO> list = matchService.getByTeamAndDate(teamName, date).stream()
                .map(matchService::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/public")
    public ResponseEntity<List<MatchResponseDTO>> getPublicMatches(
            @RequestParam(required = false) String team) {
        List<MatchResponseDTO> list = matchService.searchPublicByTeam(team).stream()
                .map(matchService::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** Todos los partidos (públicos + privados) de todos los equipos de un club.
     *  Solo accesible para usuarios autenticados (el club logueado). */
    @GetMapping("/club/{clubName}")
    public ResponseEntity<List<MatchResponseDTO>> getByClub(@PathVariable String clubName) {
        List<MatchResponseDTO> list = matchService.getMatchesByClub(clubName).stream()
                .map(matchService::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchResponseDTO> updateMatch(
            @PathVariable Long id, @RequestBody MatchCreateDTO dto) {
        return matchService.getById(id).map(match -> {
            match.setRivalTeam(dto.getRivalTeam());
            match.setDate(dto.getDate());
            match.setStartTime(dto.getStartTime());
            match.setPartDuration(dto.getPartDuration());
            match.setBreakDuration(dto.getBreakDuration());
            match.setPublic(dto.isPublic());
            match.setLocation(dto.getLocation());
            match.setManualHalftime(dto.isManualHalftime());
            return ResponseEntity.ok(matchService.toDTO(matchService.updateMatch(match)));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        matchService.getById(id).ifPresent(match -> {
            boolean finishing = "FINISHED".equals(status) && !"FINISHED".equals(match.getStatus());
            if ("LIVE".equals(status) && "HALFTIME".equals(match.getStatus())) {
                // Inicio de la 2ª parte: guardar el timestamp exacto del inicio
                // (no se toca liveStartedAt para no perder el inicio de la 1ª parte)
                match.setHalftimeEndedAt(System.currentTimeMillis());
                matchService.updateMatch(match);
            } else if ("LIVE".equals(status) && !"LIVE".equals(match.getStatus()) && match.getLiveStartedAt() == null) {
                // Primer inicio del partido
                match.setLiveStartedAt(System.currentTimeMillis());
                matchService.updateMatch(match);
            }
            matchService.updateStatus(id, status);

            if (finishing && match.getTeamName() != null) {
                // Obtener jugadores del equipo via team_members (relación @ManyToMany)
                // Esto cubre tanto el campo teamName legacy como la tabla de unión multi-equipo
                java.util.Set<String> subbedInNames = new java.util.HashSet<>();
                java.util.Set<String> subbedOutNames = new java.util.HashSet<>();
                matchService.getEvents(id).stream()
                    .filter(ev -> "SUBSTITUTION".equals(ev.getType()))
                    .forEach(ev -> {
                        if (ev.getPlayerOutName() != null) subbedOutNames.add(ev.getPlayerOutName());
                        if (ev.getPlayerInName() != null) subbedInNames.add(ev.getPlayerInName());
                    });

                teamRepo.findByName(match.getTeamName()).ifPresent(team -> {
                    team.getPlayers().forEach(p -> {
                        boolean starter  = p.getLineupX() > 0 && p.getLineupY() > 0;
                        boolean subbedIn  = subbedInNames.contains(p.getName());
                        boolean subbedOut = subbedOutNames.contains(p.getName());

                        if (starter) {
                            // Titular: jugó el partido (y lo empezó)
                            p.setMatchesPlayed(p.getMatchesPlayed() + 1);
                            p.setMatchesStarted(p.getMatchesStarted() + 1);
                            userRepo.save(p);
                        } else if (subbedIn) {
                            // Entró como sustituto: cuenta como jugado pero no como titular
                            p.setMatchesPlayed(p.getMatchesPlayed() + 1);
                            userRepo.save(p);
                        }
                    });
                });
                // Fallback: si el equipo no tiene jugadores via team_members,
                // usar el campo teamName legacy
                if (!teamRepo.findByName(match.getTeamName()).isPresent()
                        || teamRepo.findByName(match.getTeamName()).get().getPlayers().isEmpty()) {
                    userRepo.findByTeamNameAndType(match.getTeamName(), "PLAYER").forEach(p -> {
                        boolean starter  = p.getLineupX() > 0 && p.getLineupY() > 0;
                        boolean subbedIn  = subbedInNames.contains(p.getName());
                        if (starter) {
                            p.setMatchesPlayed(p.getMatchesPlayed() + 1);
                            p.setMatchesStarted(p.getMatchesStarted() + 1);
                            userRepo.save(p);
                        } else if (subbedIn) {
                            p.setMatchesPlayed(p.getMatchesPlayed() + 1);
                            userRepo.save(p);
                        }
                    });
                }
            }
        });
        try {
            org.json.JSONObject wsMsg = new org.json.JSONObject();
            wsMsg.put("type", "match_update");
            wsMsg.put("matchId", id);
            wsMsg.put("status", status);
            // Incluir datos del partido para que el cliente pueda notificar sin API call extra
            matchService.getById(id).ifPresent(m -> {
                try {
                    wsMsg.put("teamName",  m.getTeamName()  != null ? m.getTeamName()  : "");
                    wsMsg.put("rivalTeam", m.getRivalTeam() != null ? m.getRivalTeam() : "");
                    if (m.getLiveStartedAt() != null) wsMsg.put("liveStartedAt", m.getLiveStartedAt());
                    // clubName viene del equipo
                    if (m.getTeamName() != null) {
                        teamRepo.findByName(m.getTeamName()).ifPresent(t -> {
                            try { wsMsg.put("clubName", t.getClubName() != null ? t.getClubName() : ""); }
                            catch (Exception ignored2) {}
                        });
                    }
                } catch (Exception ignored2) {}
            });
            lineupWsHandler.broadcast(wsMsg.toString());
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/score")
    public ResponseEntity<Void> updateScore(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        matchService.getById(id).ifPresent(m -> {
            if (body.containsKey("goalsFor")) m.setGoalsFor(body.get("goalsFor"));
            if (body.containsKey("goalsAgainst")) m.setGoalsAgainst(body.get("goalsAgainst"));
            matchService.updateMatch(m);
        });
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<MatchEventResponseDTO>> getEvents(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getEventDTOs(id));
    }

    @PostMapping("/{id}/events")
    public ResponseEntity<MatchEventResponseDTO> addEvent(
            @PathVariable Long id, @RequestBody MatchEventCreateDTO dto) {
        MatchEvent event = new MatchEvent();
        event.setMatchId(id);
        event.setType(dto.getType());
        event.setPlayerName(dto.getPlayerName());
        event.setMinute(dto.getMinute());
        event.setSecond(dto.getSecond());
        event.setAssistPlayerName(dto.getAssistPlayerName());
        event.setPlayerOutName(dto.getPlayerOutName());
        event.setPlayerInName(dto.getPlayerInName());
        event.setHalf(dto.getHalf() > 0 ? dto.getHalf() : 1);

        MatchEvent saved = matchService.addEvent(event);

        // Actualizar estadísticas del jugador según el tipo de evento
        if ("GOAL".equals(dto.getType())) {
            if (dto.getPlayerName() != null)
                userRepo.findByName(dto.getPlayerName()).ifPresent(p -> { p.setGoals(p.getGoals() + 1); userRepo.save(p); });
            if (dto.getAssistPlayerName() != null)
                userRepo.findByName(dto.getAssistPlayerName()).ifPresent(p -> { p.setAssists(p.getAssists() + 1); userRepo.save(p); });
        }

        // Difundir evento por WebSocket
        try {
            org.json.JSONObject wsMsg = new org.json.JSONObject();
            wsMsg.put("type", "match_event");
            wsMsg.put("matchId", id);
            wsMsg.put("eventType", dto.getType());
            wsMsg.put("playerName", dto.getPlayerName() != null ? dto.getPlayerName() : "");
            wsMsg.put("minute", dto.getMinute());
            if (dto.getAssistPlayerName() != null) wsMsg.put("assistPlayerName", dto.getAssistPlayerName());
            if (dto.getPlayerOutName() != null) wsMsg.put("playerOutName", dto.getPlayerOutName());
            if (dto.getPlayerInName() != null) wsMsg.put("playerInName", dto.getPlayerInName());
            lineupWsHandler.broadcast(wsMsg.toString());
        } catch (Exception ignored) {}

        // Construir respuesta
        MatchEventResponseDTO respDto = new MatchEventResponseDTO();
        respDto.setId(saved.getId());
        respDto.setMatchId(saved.getMatchId());
        respDto.setType(saved.getType());
        respDto.setPlayerName(saved.getPlayerName());
        respDto.setMinute(saved.getMinute());
        respDto.setAssistPlayerName(saved.getAssistPlayerName());
        respDto.setPlayerOutName(saved.getPlayerOutName());
        respDto.setPlayerInName(saved.getPlayerInName());
        return ResponseEntity.status(201).body(respDto);
    }

    @DeleteMapping("/{id}/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @PathVariable Long eventId) {
        matchService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
