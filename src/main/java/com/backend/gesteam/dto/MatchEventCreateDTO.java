package com.backend.gesteam.dto;

public class MatchEventCreateDTO {
    private String type;           // "GOAL", "YELLOW_CARD", "RED_CARD", "SUBSTITUTION"
    private String playerName;
    private int minute;
    private int second;
    private String assistPlayerName;
    private String playerOutName;
    private String playerInName;
    private int half = 1;            // 1 = primera parte, 2 = segunda parte

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
