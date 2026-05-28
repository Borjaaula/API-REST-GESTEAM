package com.backend.gesteam.service.team;

import com.backend.gesteam.dto.TeamSummaryDTO;
import com.backend.gesteam.entity.Team;
import com.backend.gesteam.entity.User;
import com.backend.gesteam.repository.impl.TeamRepository;
import com.backend.gesteam.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserService userService;

    /**
     * Crear un nuevo equipo
     */
    public Team createTeam(String name, String clubName, User coach) {
        Team team = new Team(name, clubName, coach);
        Team saved = teamRepository.addTeam(team);
        return teamRepository.getTeamByName(name).orElse(saved);
    }

    /**
     * Obtener equipo por nombre
     */
    public Optional<Team> getTeamByName(String name) {
        return teamRepository.getTeamByName(name);
    }

    /**
     * Obtener todos los equipos
     */
    public List<Team> getAllTeams() {
        return teamRepository.getAllTeams();
    }

    /**
     * Obtener equipos por nombre del club
     */
    public List<Team> getTeamsByClubName(String clubName) {
        return teamRepository.getTeamsByClubName(clubName);
    }

    /**
     * Obtener equipos accesibles para un administrador de club
     */
    public List<Team> getTeamsForClubAdmin(User admin) {
        List<Team> teams = teamRepository.getTeamsByClubAdmin(admin);
        // Forzar inicialización de la colección lazy de players para que el mapper
        // pueda acceder a los datos de los coaches sin LazyInitializationException
        teams.forEach(t -> t.getPlayers().size());
        return teams;
    }

    /**
     * Actualizar información del equipo
     */
    public Team updateTeam(Team team) {
        return teamRepository.updateTeam(team);
    }

    /**
     * Cambiar nombre del club de un equipo
     */
    public void updateTeamClubName(String teamName, String clubName) {
        teamRepository.updateTeamClubName(teamName, clubName);
    }

    /**
     * Actualizar visibilidad de alineación
     */
    public void updateLineupVisibility(String teamName, boolean visible) {
        teamRepository.updateTeamLineupVisibility(teamName, visible);
    }

    /**
     * Actualizar entrenador del equipo
     */
    public void updateTeamCoach(String teamName, User coach) {
        teamRepository.updateTeamCoach(teamName, coach);
    }

    /**
     * Agregar jugador al equipo
     */
    public void addPlayerToTeam(String teamName, User player) {
        Optional<Team> team = teamRepository.getTeamByName(teamName);
        if (team.isEmpty()) return;

        boolean alreadyInTeam = team.get().getPlayers().stream()
                .anyMatch(p -> p.getName() != null && p.getName().equals(player.getName()));
        if (alreadyInTeam) {
            throw new com.backend.gesteam.exceptions.GesteamBusinessException(
                    "El usuario '" + player.getName() + "' ya pertenece al equipo '" + teamName + "'");
        }

        teamRepository.addPlayerToTeam(teamName, player);
        userService.updateUserTeam(player.getName(), teamName);
    }

    /**
     * Remover jugador del equipo
     */
    public void removePlayerFromTeam(String teamName, Long userId) {
        teamRepository.removePlayerFromTeam(teamName, userId);
    }

    /**
     * Obtener los equipos en los que está un usuario (jugador o entrenador).
     * Cubre team_members (ManyToMany) y coach_id FK.
     */
    public List<Team> getTeamsByPlayerName(String playerName) {
        List<Team> teams = teamRepository.getTeamsByPlayerName(playerName);
        // Forzar inicialización de la colección lazy de players dentro de @Transactional
        // para evitar LazyInitializationException al serializar en el mapper/controller
        teams.forEach(t -> t.getPlayers().size());
        return teams;
    }

    /**
     * Obtener jugadores de un equipo
     */
    public List<User> getTeamPlayers(String teamName) {
        return teamRepository.getTeamPlayers(teamName);
    }

    /**
     * Obtener jugadores por posición en alineación (para visualización)
     */
    public List<User> getPlayersInLineup(String teamName) {
        List<User> players = getTeamPlayers(teamName);
        return players.stream()
                .filter(p -> p.getLineupX() >= 0 && p.getLineupY() >= 0)
                .toList();
    }

    /**
     * Eliminar equipo
     */
    public void deleteTeam(String teamName) {
        teamRepository.deleteTeam(teamName);
    }

    /**
     * Eliminar todos los equipos de un club (cascade al borrar cuenta de club)
     */
    public void deleteTeamsByClub(String clubName) {
        List<Team> teams = getTeamsByClubName(clubName);
        for (Team team : teams) {
            teamRepository.deleteTeam(team.getName());
        }
    }

    /**
     * Verificar si existe un equipo
     */
    public boolean teamExists(String teamName) {
        return teamRepository.teamExists(teamName);
    }

    /**
     * Obtener cantidad de jugadores en un equipo
     */
    public int getTeamPlayerCount(String teamName) {
        return getTeamPlayers(teamName).size();
    }

    /**
     * Aplicar actualización remota: actualizar equipo y sus jugadores
     */
    public void applyRemoteUpdate(Team updatedTeam) {
        if (updatedTeam == null || updatedTeam.getName() == null) {
            return;
        }

        // Guardar/actualizar equipo
        teamRepository.updateTeam(updatedTeam);

        // Actualizar estadísticas y posiciones de jugadores
        for (User player : updatedTeam.getPlayers()) {
            Optional<User> existingPlayer = userService.getUserByName(player.getName());
            if (existingPlayer.isPresent()) {
                userService.updateUserStats(player.getName(),
                    player.getMatchesPlayed(),
                    player.getMatchesStarted(),
                    player.getGoals(),
                    player.getAssists());

                userService.updateUserLineupPosition(player.getName(),
                    player.getLineupX(),
                    player.getLineupY());

                userService.updateUserTeam(player.getName(), updatedTeam.getName());
            }
        }
    }

    /**
     * Obtener resumen de equipo (con estadísticas agregadas)
     */
    public TeamSummaryDTO getTeamSummary(String teamName) {
        Optional<Team> team = teamRepository.getTeamByName(teamName);
        if (team.isEmpty()) {
            return null;
        }

        Team t = team.get();
        List<User> players = t.getPlayers();

        int totalGoals = players.stream().mapToInt(User::getGoals).sum();
        int totalAssists = players.stream().mapToInt(User::getAssists).sum();
        int totalMatches = players.isEmpty() ? 0 : players.get(0).getMatchesPlayed();

        String coachNames = t.getCoach() != null ? t.getCoach().getName() : "";

        return new TeamSummaryDTO(
            t.getName(),
            t.getClubName(),
            coachNames.isEmpty() ? null : coachNames,
            players.size(),
            totalGoals,
            totalAssists,
            totalMatches,
            t.isLineupVisible()
        );
    }
}
