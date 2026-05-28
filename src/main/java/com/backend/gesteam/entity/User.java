package com.backend.gesteam.entity;

import com.backend.gesteam.enums.PlayerPosition;
import com.backend.gesteam.enums.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a cualquier usuario del sistema.
 * El campo {@code type} determina su rol: CLUB, COACH, PLAYER o USER (sin club).
 * El campo {@code status} controla el acceso al club: APPROVED, PENDING o REJECTED.
 * Las coordenadas {@code lineupX/lineupY} (valores 0–1 normalizados) indican
 * su posición en el campo; -1 significa que está en el banquillo.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private UserType type;

    private String teamName;

    private String clubName;

    private String status;

    private String password;

    private String email;

    private int age;

    private float height;

    private float weight;

    private String profileImageUri;

    private int matchesPlayed;

    private int matchesStarted;

    private int goals;

    private int assists;

    private String phone;

    private int trainingAttendanceCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PlayerPosition position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PlayerPosition secondaryPosition;

    @Lob
    @Column(name = "profile_image", columnDefinition = "MEDIUMBLOB")
    private byte[] profileImage;

    @Column(name = "lineup_x")
    private Float lineupX = -1f;

    @Column(name = "lineup_y")
    private Float lineupY = -1f;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @JsonIgnore
    @ManyToMany(mappedBy = "players")
    private List<Team> teams = new ArrayList<>();

    // Constructores
    public User() {}

    public User(String name, UserType type, String teamName, String password) {
        this.name = name;
        this.type = type;
        this.teamName = teamName;
        this.password = password;
        this.status = "APPROVED";
    }

    public User(String name, UserType type, String teamName, String clubName, String status, String password) {
        this.name = name;
        this.type = type;
        this.teamName = teamName;
        this.clubName = clubName;
        this.status = status;
        this.password = password;
    }

    public User(String name, UserType type, String teamName, String clubName, String status, String password, String email, int age, float height, float weight) {
        this.name = name;
        this.type = type;
        this.teamName = teamName;
        this.clubName = clubName;
        this.status = status;
        this.password = password;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public User(String name, UserType type, String teamName, String clubName, String status, String password, String email, int age, float height, float weight, String profileImageUri, int matchesPlayed, int matchesStarted, int goals, int assists) {
        this.name = name;
        this.type = type;
        this.teamName = teamName;
        this.clubName = clubName;
        this.status = status;
        this.password = password;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.profileImageUri = profileImageUri;
        this.matchesPlayed = matchesPlayed;
        this.matchesStarted = matchesStarted;
        this.goals = goals;
        this.assists = assists;
    }

    public User(String name, UserType type, String teamName, String clubName, String status, String password, String email, int age, float height, float weight, String profileImageUri, int matchesPlayed, int matchesStarted, int goals, int assists, String phone) {
        this.name = name;
        this.type = type;
        this.teamName = teamName;
        this.clubName = clubName;
        this.status = status;
        this.password = password;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.profileImageUri = profileImageUri;
        this.matchesPlayed = matchesPlayed;
        this.matchesStarted = matchesStarted;
        this.goals = goals;
        this.assists = assists;
        this.phone = phone;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UserType getType() { return type; }
    public void setType(UserType type) { this.type = type; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public String getProfileImageUri() { return profileImageUri; }
    public void setProfileImageUri(String profileImageUri) { this.profileImageUri = profileImageUri; }

    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }

    public int getMatchesStarted() { return matchesStarted; }
    public void setMatchesStarted(int matchesStarted) { this.matchesStarted = matchesStarted; }

    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }

    public int getAssists() { return assists; }
    public void setAssists(int assists) { this.assists = assists; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getTrainingAttendanceCount() { return trainingAttendanceCount; }
    public void setTrainingAttendanceCount(int trainingAttendanceCount) { this.trainingAttendanceCount = trainingAttendanceCount; }

    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }

    public float getLineupX() { return lineupX != null ? lineupX : -1f; }
    public void setLineupX(Float lineupX) { this.lineupX = lineupX; }

    public float getLineupY() { return lineupY != null ? lineupY : -1f; }
    public void setLineupY(Float lineupY) { this.lineupY = lineupY; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }

    public PlayerPosition getPosition() { return position; }
    public void setPosition(PlayerPosition position) { this.position = position; }

    public PlayerPosition getSecondaryPosition() { return secondaryPosition; }
    public void setSecondaryPosition(PlayerPosition secondaryPosition) { this.secondaryPosition = secondaryPosition; }

}
