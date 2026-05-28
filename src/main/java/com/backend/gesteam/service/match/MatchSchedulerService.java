package com.backend.gesteam.service.match;

import com.backend.gesteam.config.LineupWebSocketHandler;
import com.backend.gesteam.entity.Match;
import com.backend.gesteam.repository.MatchJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tarea programada que comprueba cada minuto si algún partido
 * con estado SCHEDULED ha llegado a su hora y lo pone en LIVE.
 */
@Service
public class MatchSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(MatchSchedulerService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private MatchJpaRepository matchRepo;

    @Autowired
    private LineupWebSocketHandler lineupWsHandler;

    /**
     * Se ejecuta cada 30 segundos. Busca partidos SCHEDULED cuya hora programada
     * ya haya llegado y los pone en LIVE automáticamente.
     *
     * IMPORTANTE: NO se anota con @Transactional aquí a propósito.
     * matchRepo.save() usa su propia transacción (@Transactional del repositorio JPA)
     * y hace commit antes de que retorne. Así el broadcast por WebSocket
     * siempre ocurre DESPUÉS del commit en BD, evitando la race condition
     * en la que el cliente Android recibía el WS antes de ver el cambio en la API.
     */
    @Scheduled(fixedDelay = 30_000)
    public void autoStartScheduledMatches() {
        List<Match> scheduled = matchRepo.findByStatus("SCHEDULED");
        if (scheduled.isEmpty()) return;

        long now = System.currentTimeMillis();

        for (Match m : scheduled) {
            if (m.getDate() == null || m.getStartTime() == null || m.getStartTime().isBlank()) continue;
            try {
                String dt = m.getDate() + " " + m.getStartTime();
                LocalDateTime ldt = LocalDateTime.parse(dt, FMT);
                long matchEpoch = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                if (now >= matchEpoch) {
                    m.setStatus("LIVE");
                    m.setLiveStartedAt(matchEpoch); // hora programada → cronómetro preciso
                    matchRepo.save(m); // commit inmediato (transacción del repositorio)

                    log.info("Partido {} ({} vs {}) iniciado automáticamente a las {}",
                            m.getId(), m.getTeamName(), m.getRivalTeam(), ldt);

                    // WS broadcast DESPUÉS del commit → no hay race condition
                    try {
                        org.json.JSONObject wsMsg = new org.json.JSONObject();
                        wsMsg.put("type", "match_update");
                        wsMsg.put("matchId", m.getId());
                        wsMsg.put("status", "LIVE");
                        wsMsg.put("liveStartedAt", matchEpoch); // incluir para que Android no necesite otra petición
                        lineupWsHandler.broadcast(wsMsg.toString());
                    } catch (Exception wsEx) {
                        log.warn("Error enviando WS para partido {}: {}", m.getId(), wsEx.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("Error comprobando auto-inicio del partido {}: {}", m.getId(), e.getMessage());
            }
        }
    }
}
