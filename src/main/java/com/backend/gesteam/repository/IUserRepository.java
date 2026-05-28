package com.backend.gesteam.repository;

import com.backend.gesteam.entity.User;
import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    User addUser(User user);
    Optional<User> getUserByName(String username);
    User loginUser(String username, String password);
    List<User> getAllUsers();
    List<User> getPlayersByTeamName(String teamName);
    List<User> getUsersByClubName(String clubName);
    List<User> getPendingUsersByClub(String clubName);
    User updateUser(User user);
    void updateUserStatus(String username, String status);
    void updateUserTeam(String username, String teamName);
    void updateUserClub(String username, String clubName);
    void updateUserLineupPosition(String username, float x, float y);
    void updateUserStats(String username, int matchesPlayed, int matchesStarted, int goals, int assists);
    void updateUserAttributes(String username, int age, float height, float weight);
    void updateUserType(String username, String type);
    void deleteUser(String username);
    List<String> getAllClubNames();
    void updateUserProfile(User user);
    List<User> getUsersByStatus(String status);
}
