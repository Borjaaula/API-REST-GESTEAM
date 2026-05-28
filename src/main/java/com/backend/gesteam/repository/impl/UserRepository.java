package com.backend.gesteam.repository.impl;

import com.backend.gesteam.entity.User;
import com.backend.gesteam.enums.UserType;
import com.backend.gesteam.repository.IUserRepository;
import com.backend.gesteam.repository.TeamJpaRepository;
import com.backend.gesteam.repository.UserJpaRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

@Repository
public class UserRepository implements IUserRepository {

    @Autowired
    private UserJpaRepository jpaRepository;

    @Autowired
    private TeamJpaRepository teamJpaRepository;

    @Autowired
    private DSLContext dsl;

    // ===== JPA-based operations =====

    public User addUser(User user) {
        return jpaRepository.save(user);
    }

    public Optional<User> getUserByName(String username) {
        return jpaRepository.findByName(username);
    }

    public User loginUser(String username, String password) {
        Optional<User> user = jpaRepository.findByName(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        return null;
    }

    public List<User> getAllUsers() {
        return jpaRepository.findAll();
    }

    public List<User> getPlayersByTeamName(String teamName) {
        // Usar relación JPA directa (más fiable que query por campo teamName)
        return teamJpaRepository.findByName(teamName)
                .map(team -> team.getPlayers().stream()
                        .filter(u -> u.getType() == UserType.PLAYER)
                        .collect(Collectors.toList()))
                .orElseGet(() -> jpaRepository.findByTeamNameAndType(teamName, UserType.PLAYER.name()));
    }

    public List<User> getUsersByClubName(String clubName) {
        return jpaRepository.findByClubName(clubName);
    }

    public List<User> getPendingUsersByClub(String clubName) {
        return jpaRepository.findByClubName(clubName).stream()
                .filter(u -> "PENDING".equals(u.getStatus()))
                .toList();
    }

    public User updateUser(User user) {
        return jpaRepository.save(user);
    }

    public void updateUserStatus(String username, String status) {
        Optional<User> user = jpaRepository.findByName(username);
        if (user.isPresent()) {
            User u = user.get();
            u.setStatus(status);
            jpaRepository.save(u);
        }
    }

    public void updateUserTeam(String username, String teamName) {
        Optional<User> user = jpaRepository.findByName(username);
        if (user.isPresent()) {
            User u = user.get();
            u.setTeamName(teamName != null ? teamName : "");
            if (teamName == null || teamName.trim().isEmpty()) {
                u.setTeam(null);
                u.setLineupX(-1f);
                u.setLineupY(-1f);
            }
            jpaRepository.save(u);
        }
    }

    public void updateUserClub(String username, String clubName) {
        Optional<User> user = jpaRepository.findByName(username);
        if (user.isPresent()) {
            User u = user.get();
            u.setClubName(clubName);
            jpaRepository.save(u);
        }
    }

    public void updateUserLineupPosition(String username, float x, float y) {
        Optional<User> user = jpaRepository.findByName(username);
        if (user.isPresent()) {
            User u = user.get();
            u.setLineupX(x);
            u.setLineupY(y);
            jpaRepository.save(u);
        }
    }

    public void updateUserStats(String username, int matchesPlayed, int matchesStarted, int goals, int assists) {
        Optional<User> user = jpaRepository.findByName(username);
        if (user.isPresent()) {
            User u = user.get();
            u.setMatchesPlayed(matchesPlayed);
            u.setMatchesStarted(matchesStarted);
            u.setGoals(goals);
            u.setAssists(assists);
            jpaRepository.save(u);
        }
    }

    public void updateUserAttributes(String username, int age, float height, float weight) {
        Optional<User> user = jpaRepository.findByName(username);
        if (user.isPresent()) {
            User u = user.get();
            u.setAge(age);
            u.setHeight(height);
            u.setWeight(weight);
            jpaRepository.save(u);
        }
    }

    public void updateUserType(String username, String type) {
        Optional<User> user = jpaRepository.findByName(username);
        if (user.isPresent()) {
            User u = user.get();
            u.setType(UserType.valueOf(type));
            jpaRepository.save(u);
        }
    }

    public void deleteUser(String username) {
        Optional<User> user = jpaRepository.findByName(username);
        user.ifPresent(jpaRepository::delete);
    }

    public List<String> getAllClubNames() {
        return jpaRepository.findAll().stream()
                .map(User::getClubName)
                .distinct()
                .filter(name -> name != null && !name.isEmpty())
                .toList();
    }

    public void updateUserProfile(User user) {
        jpaRepository.save(user);
    }

    public List<User> getUsersByStatus(String status) {
        return jpaRepository.findByStatus(status);
    }
}
