package com.backend.gesteam.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String clubName;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private User coach;

    // Relación ManyToMany: un jugador puede pertenecer a varios equipos (tabla join team_members)
    @ManyToMany
    @JoinTable(
        name = "team_members",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> players = new ArrayList<>();

    private boolean isLineupVisible;

    private String profileImageUri;

    @Lob
    @Column(name = "team_profile_image", columnDefinition = "MEDIUMBLOB")
    private byte[] profileImage;

    // Constructores
    public Team() {
        this.isLineupVisible = false;
    }

    public Team(String name, User coach) {
        this.name = name;
        this.coach = coach;
        this.isLineupVisible = false;
    }

    public Team(String name, String clubName, User coach) {
        this.name = name;
        this.clubName = clubName;
        this.coach = coach;
        this.isLineupVisible = false;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public User getCoach() { return coach; }
    public void setCoach(User coach) { this.coach = coach; }

    public List<User> getPlayers() { return players; }
    public void setPlayers(List<User> players) { this.players = players; }

    public void addPlayer(User player) {
        if (!players.contains(player)) players.add(player);
    }

    public void removePlayer(User player) {
        players.remove(player);
    }

    public boolean isLineupVisible() { return isLineupVisible; }
    public void setLineupVisible(boolean lineupVisible) { isLineupVisible = lineupVisible; }

    public String getProfileImageUri() { return profileImageUri; }
    public void setProfileImageUri(String profileImageUri) { this.profileImageUri = profileImageUri; }

    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }
}
