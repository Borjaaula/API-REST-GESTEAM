package com.backend.gesteam.repository;

import com.backend.gesteam.entity.Team;
import com.backend.gesteam.entity.User;
import java.util.List;
import java.util.Optional;

public interface ITeamRepository {
    Team addTeam(Team team);
    Optional<Team> getTeamByName(String name);
    List<Team> getAllTeams();
    List<Team> getTeamsByClubName(String clubName);
    List<Team> getTeamsByClubAdmin(User admin);
    Team updateTeam(Team team);
    void deleteTeam(String teamName);
    void updateTeamLineupVisibility(String teamName, boolean visible);
    void updateTeamCoach(String teamName, User coach);
    void addPlayerToTeam(String teamName, User player);
    void removePlayerFromTeam(String teamName, Long userId);
    List<User> getTeamPlayers(String teamName);
    List<Team> getTeamsByPlayerName(String playerName);
    void updateTeamClubName(String teamName, String clubName);
    boolean teamExists(String teamName);

}
