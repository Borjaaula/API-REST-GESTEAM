package com.backend.gesteam.entity;

import jakarta.persistence.*;

/**
 * Evento ocurrido durante un partido (gol, tarjeta, sustitución...).
 * El campo {@code half} indica la parte del partido (1 o 2) en que ocurrió el evento.
 * Para sustituciones se usan {@code playerOutName} y {@code playerInName};
 * para goles, {@code assistPlayerName} es opcional.
 */
@Entity
@Table(name = "match_events")
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long matchId;
    private String type;            // "GOAL", "YELLOW_CARD", "RED_CARD", "SUBSTITUTION"
    private String playerName;
    private int minute;
    private int second;             // segundo dentro del minuto
    private String assistPlayerName;  // for goals
    private String playerOutName;     // for substitutions
    private String playerInName;      // for substitutions
    private int half = 1;             // 1 = primera parte, 2 = segunda parte

    public MatchEvent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public int getSecond() { return second; }
    public void setSecond(int second) { this.second = second; }

    public String getAssistPlayerName() { return assistPlayerName; }
    public void setAssistPlayerName(String assistPlayerName) { this.assistPlayerName = assistPlayerName; }

    public String getPlayerOutName() { return playerOutName; }
    public void setPlayerOutName(String playerOutName) { this.playerOutName = playerOutName; }

    public String getPlayerInName() { return playerInName; }
    public void setPlayerInName(String playerInName) { this.playerInName = playerInName; }

    public int getHalf() { return half; }
    public void setHalf(int half) { this.half = half; }
}
