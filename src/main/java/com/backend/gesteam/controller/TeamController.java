package com.backend.gesteam.controller;

import com.backend.gesteam.dto.TeamCoachDTO;
import com.backend.gesteam.dto.TeamClubDTO;
import com.backend.gesteam.dto.TeamCreateDTO;
import com.backend.gesteam.dto.TeamSummaryDTO;
import com.backend.gesteam.dto.TeamVisibilityDTO;
import com.backend.gesteam.dto.TeamUpdateDTO;
import com.backend.gesteam.dto.TeamResponseDTO;
import com.backend.gesteam.dto.UserResponseDTO;
import com.backend.gesteam.entity.Team;
import com.backend.gesteam.entity.User;
import com.backend.gesteam.exceptions.ResourceNotFoundException;
import com.backend.gesteam.mappers.TeamMapper;
import com.backend.gesteam.mappers.UserMapper;
import com.backend.gesteam.service.team.TeamService;
import com.backend.gesteam.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private com.backend.gesteam.repository.impl.TeamRepository teamRepository;

    /**
     * Crear nuevo equipo
     */
    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamCreateDTO teamDTO) {
        User coach = null;
        if (teamDTO.getCoachName() != null && !teamDTO.getCoachName().isEmpty()) {
            Optional<User> optionalCoach = userService.getUserByName(teamDTO.getCoachName());
            if (optionalCoach.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            coach = optionalCoach.get();
        }

        Team team = teamService.createTeam(teamDTO.getName(), teamDTO.getClubName(), coach);

        return ResponseEntity.status(201).body(teamMapper.toDTO(team));
    }

    @PutMapping("/{teamName}")
    public ResponseEntity<TeamResponseDTO> updateTeam(
            @PathVariable String teamName,
            @Valid @RequestBody TeamUpdateDTO teamDTO,
            HttpServletRequest request) {
        Optional<Team> existing = teamService.getTeamByName(teamName);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("El equipo '" + teamName + "' no existe");
        }

        Team team = existing.get();
        String newName = teamDTO.getName() != null ? teamDTO.getName() : team.getName();
        team.setName(newName);
        team.setClubName(teamDTO.getClubName() != null ? teamDTO.getClubName() : team.getClubName());
        team.setLineupVisible(teamDTO.getLineupVisible());

        if (teamDTO.getProfileImageUri() != null) {
            team.setProfileImageUri(teamDTO.getProfileImageUri());
        }

        // Si se renombra y tiene imagen binaria almacenada, actualizar la URI al nuevo nombre
        if (!newName.equals(teamName) && team.getProfileImage() != null && team.getProfileImage().length > 0) {
            String newImageUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + "/api/teams/" + newName + "/profile-image";
            team.setProfileImageUri(newImageUrl);
        }

        Team updated = teamService.updateTeam(team);
        return ResponseEntity.ok(teamMapper.toDTO(updated));
    }

    @PatchMapping("/{teamName}/club")
    public ResponseEntity<Void> updateTeamClubName(@PathVariable String teamName, @Valid @RequestBody TeamClubDTO clubDTO) {
        teamService.updateTeamClubName(teamName, clubDTO.getClubName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener equipo por nombre
     */
    @GetMapping("/{teamName}")
    public ResponseEntity<TeamResponseDTO> getTeamByName(@PathVariable String teamName) {
        return teamService.getTeamByName(teamName)
                .map(team -> ResponseEntity.ok(teamMapper.toDTO(team)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtener todos los equipos
     */
    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {
        List<TeamResponseDTO> teams = teamService.getAllTeams().stream()
                .map(teamMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    /**
     * Obtener equipos por club
     */
    @GetMapping("/club/{clubName}")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsByClub(@PathVariable String clubName) {
        List<TeamResponseDTO> teams = teamService.getTeamsByClubName(clubName).stream()
                .map(teamMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    /**
     * Obtener equipos accesibles para un administrador de club
     */
    @GetMapping("/admin/{adminName}")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsForAdmin(@PathVariable String adminName) {
        Optional<User> admin = userService.getUserByName(adminName);
        if (admin.isEmpty()) {
            throw new ResourceNotFoundException("El administrador '" + adminName + "' no fue encontrado");
        }

        List<TeamResponseDTO> teams = teamService.getTeamsForClubAdmin(admin.get()).stream()
                .map(teamMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    /**
     * Obtener jugadores de un equipo
     */
    @GetMapping("/{teamName}/players")
    public ResponseEntity<List<UserResponseDTO>> getTeamPlayers(@PathVariable String teamName) {
        List<UserResponseDTO> players = teamService.getTeamPlayers(teamName).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(players);
    }

    /**
     * Obtener jugadores en alineación
     */
    @GetMapping("/{teamName}/lineup")
    public ResponseEntity<List<UserResponseDTO>> getPlayersInLineup(@PathVariable String teamName) {
        List<UserResponseDTO> players = teamService.getPlayersInLineup(teamName).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(players);
    }

    /**
     * Agregar jugador al equipo
     */
    @PostMapping("/{teamName}/players/{playerName}")
    public ResponseEntity<Void> addPlayerToTeam(
            @PathVariable String teamName,
            @PathVariable String playerName) {
        Optional<User> player = userService.getUserByName(playerName);
        if (player.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        teamService.addPlayerToTeam(teamName, player.get());
        return ResponseEntity.noContent().build();
    }

    /**
     * Remover jugador del equipo
     */
    @DeleteMapping("/{teamName}/players/{playerId}")
    public ResponseEntity<Void> removePlayerFromTeam(
            @PathVariable String teamName,
            @PathVariable Long playerId) {
        teamService.removePlayerFromTeam(teamName, playerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualizar visibilidad de alineación
     */
    @PatchMapping("/{teamName}/lineup-visibility")
    public ResponseEntity<Void> updateLineupVisibility(
            @PathVariable String teamName,
            @Valid @RequestBody TeamVisibilityDTO visibilityDTO) {
        teamService.updateLineupVisibility(teamName, visibilityDTO.getVisible());
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualizar entrenador del equipo
     */
    @PatchMapping("/{teamName}/coach")
    public ResponseEntity<Void> updateTeamCoach(
            @PathVariable String teamName,
            @Valid @RequestBody TeamCoachDTO coachDTO) {
        Optional<User> coach = userService.getUserByName(coachDTO.getCoachName());
        if (coach.isEmpty()) {
            throw new ResourceNotFoundException("El entrenador '" + coachDTO.getCoachName() + "' no fue encontrado");
        }
        teamService.updateTeamCoach(teamName, coach.get());
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener resumen de equipo
     */
    @GetMapping("/{teamName}/summary")
    public ResponseEntity<TeamSummaryDTO> getTeamSummary(@PathVariable String teamName) {
        TeamSummaryDTO summary = teamService.getTeamSummary(teamName);
        if (summary != null) {
            return ResponseEntity.ok(summary);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Eliminar equipo
     */
    @DeleteMapping("/{teamName}")
    public ResponseEntity<Void> deleteTeam(@PathVariable String teamName) {
        teamService.deleteTeam(teamName);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener equipos en los que está un jugador/entrenador (por nombre).
     * Busca en team_members (ManyToMany) Y en el FK team_id legado.
     */
    @GetMapping("/player/{playerName}")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsByPlayer(@PathVariable String playerName) {
        List<TeamResponseDTO> teams = teamService.getTeamsByPlayerName(playerName).stream()
                .map(teamMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }
}
