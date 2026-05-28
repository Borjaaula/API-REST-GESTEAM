package com.backend.gesteam.repository.impl;

import com.backend.gesteam.entity.Team;
import com.backend.gesteam.entity.User;
import com.backend.gesteam.repository.ITeamRepository;
import com.backend.gesteam.repository.TeamJpaRepository;
import com.backend.gesteam.repository.UserJpaRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepository implements ITeamRepository {

    @Autowired
    private TeamJpaRepository jpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DSLContext dsl;

    public Team addTeam(Team team) {
        return jpaRepository.save(team);
    }

    public Optional<Team> getTeamByName(String name) {
        return jpaRepository.findByName(name);
    }

    public List<Team> getAllTeams() {
        return jpaRepository.findAll();
    }

    public List<Team> getTeamsByClubName(String clubName) {
        return jpaRepository.findByClubName(clubName);
    }

    public Team updateTeam(Team team) {
        return jpaRepository.save(team);
    }

    public void deleteTeam(String teamName) {
        Optional<Team> team = jpaRepository.findByName(teamName);
        team.ifPresent(jpaRepository::delete);
    }

    public void updateTeamLineupVisibility(String teamName, boolean visible) {
        Optional<Team> team = jpaRepository.findByName(teamName);
        if (team.isPresent()) {
            Team t = team.get();
            t.setLineupVisible(visible);
            jpaRepository.save(t);
        }
    }

    public void updateTeamCoach(String teamName, User coach) {
        Optional<Team> team = jpaRepository.findByName(teamName);
        if (team.isPresent()) {
            Team t = team.get();
            t.setCoach(coach);
            jpaRepository.save(t);
        }
    }

    public void addPlayerToTeam(String teamName, User player) {
        Optional<Team> team = jpaRepository.findByName(teamName);
        if (team.isPresent()) {
            Team t = team.get();
            if (!t.getPlayers().contains(player)) {
                t.addPlayer(player);
                jpaRepository.save(t);
            }
        }
    }

    public void removePlayerFromTeam(String teamName, Long userId) {
        Optional<Team> teamOpt = jpaRepository.findByName(teamName);
        if (teamOpt.isEmpty()) return;
        Team t = teamOpt.get();

        // 1. Quitar de team_members (ManyToMany)
        t.getPlayers().removeIf(p -> p.getId().equals(userId));

        // 2. Si el usuario era el coach del equipo (coach_id FK), limpiar también ese vínculo
        if (t.getCoach() != null && t.getCoach().getId().equals(userId)) {
            t.setCoach(null);
        }

        jpaRepository.save(t);

        // 3. Limpiar FK team_id y campo teamName en el usuario (para compatibilidad legacy)
        userJpaRepository.findById(userId).ifPresent(u -> {
            // Si team_id apunta a este equipo, nulificarlo
            if (u.getTeam() != null && teamOpt.get().getId().equals(u.getTeam().getId())) {
                u.setTeam(null);
            }
            // Recalcular teamName según equipos restantes (vía JPQL que cubre las tres fuentes)
            List<Team> remaining = jpaRepository.findTeamsByPlayerName(u.getName() != null ? u.getName() : "__none__")
                    .stream()
                    .filter(tt -> !tt.getName().equals(teamName))
                    .collect(java.util.stream.Collectors.toList());
            u.setTeamName(remaining.isEmpty() ? null : remaining.get(0).getName());
            userJpaRepository.save(u);
        });
    }

    public List<Team> getTeamsByPlayerName(String playerName) {

        return jpaRepository.findByPlayers_Name(playerName);
    }

    public List<User> getTeamPlayers(String teamName) {
        Optional<Team> team = jpaRepository.findByName(teamName);
        return team.map(Team::getPlayers).orElse(List.of());
    }

    public List<Team> getTeamsByClubAdmin(User admin) {
        if (admin == null || admin.getName() == null) {
            return getAllTeams();
        }

        String adminName = admin.getName();
        com.backend.gesteam.enums.UserType adminType = admin.getType();

        if (adminType == com.backend.gesteam.enums.UserType.COACH) {
            // Entrenador: buscar en AMBAS formas de asignación:
            //   1) team.coach FK (asignado como entrenador oficial del equipo)
            //   2) team_members join table (añadido vía "Gestionar Entidades")
            java.util.Set<String> seen = new java.util.LinkedHashSet<>();
            return getAllTeams().stream()
                    .filter(t -> {
                        boolean byCoachFk    = t.getCoach() != null && adminName.equals(t.getCoach().getName());
                        boolean byMembership = t.getPlayers().stream().anyMatch(p -> adminName.equals(p.getName()));
                        return (byCoachFk || byMembership) && seen.add(t.getName());
                    })
                    .toList();
        } else {
            // Club admin: devolver todos los equipos de su club (clubName == adminName)
            return getAllTeams().stream()
                    .filter(t -> adminName.equals(t.getClubName()))
                    .toList();
        }
    }

    public void updateTeamClubName(String teamName, String clubName) {
        Optional<Team> team = jpaRepository.findByName(teamName);
        if (team.isPresent()) {
            Team t = team.get();
            t.setClubName(clubName);
            jpaRepository.save(t);
        }
    }

    public boolean teamExists(String teamName) {
        return jpaRepository.findByName(teamName).isPresent();
    }
}
