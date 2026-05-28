package com.backend.gesteam.controller;

import com.backend.gesteam.dto.JoinRequestDTO;
import com.backend.gesteam.dto.ChangePasswordDTO;
import com.backend.gesteam.dto.LoginRequestDTO;
import com.backend.gesteam.config.security.JwtTokenProvider;
import com.backend.gesteam.config.security.SecurityUserDetails;
import com.backend.gesteam.dto.PendingRequestDTO;
import com.backend.gesteam.dto.LoginResponseDTO;
import com.backend.gesteam.dto.UserAttributesDTO;
import com.backend.gesteam.dto.UserCreateDTO;
import com.backend.gesteam.dto.UserLineupPositionDTO;
import com.backend.gesteam.dto.UserPositionDTO;
import com.backend.gesteam.dto.UserProfileUpdateDTO;
import com.backend.gesteam.dto.UserRoleDTO;
import com.backend.gesteam.dto.UserStatsDTO;
import com.backend.gesteam.dto.UserStatusDTO;
import com.backend.gesteam.dto.UserTeamDTO;
import com.backend.gesteam.dto.UserResponseDTO;
import com.backend.gesteam.entity.User;
import com.backend.gesteam.enums.PlayerPosition;
import com.backend.gesteam.exceptions.ResourceNotFoundException;
import com.backend.gesteam.mappers.UserMapper;
import com.backend.gesteam.service.user.UserService;
import com.backend.gesteam.service.team.TeamService;
import com.backend.gesteam.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    // Almacén en memoria de códigos de invitación: clubUsername → {code, expiryMs}
    private static final ConcurrentHashMap<String, String[]> inviteCodes = new ConcurrentHashMap<>();
    private static final long INVITE_TTL_MS = 10 * 60 * 1000L;

    private static String generateInviteCodeValue() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(6);
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    private final UserService userService;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private com.backend.gesteam.config.LineupWebSocketHandler lineupWsHandler;
    @Autowired
    private TeamService teamService;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, SimpMessagingTemplate messagingTemplate, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.messagingTemplate = messagingTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Login de usuario
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return userService.authenticate(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
                .map(currentUser -> {
                    String token = jwtTokenProvider.generateToken(new SecurityUserDetails(currentUser));
                    UserResponseDTO userDTO = userMapper.toDTO(currentUser);

                    LoginResponseDTO body = new LoginResponseDTO(
                            JwtTokenProvider.TOKEN_TYPE,
                            token,
                            userDTO,
                            null
                    );

                    return ResponseEntity.ok()
                            .header(HttpHeaders.AUTHORIZATION, JwtTokenProvider.TOKEN_PREFIX + token)
                            .body(body);
                })
                .orElseGet(() -> ResponseEntity.status(401).body(
                        new LoginResponseDTO(null, null, null, "Usuario o contraseña incorrectos")));
    }

    /**
     * Crear nuevo usuario
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateDTO userDTO) {
        User user = userService.createUserWithDetails(
            userDTO.getName(),
            userDTO.getType(),
            userDTO.getTeamName(),
            userDTO.getClubName(),
            userDTO.getStatus() != null ? userDTO.getStatus() : "APPROVED",
            userDTO.getPassword(),
            userDTO.getEmail(),
            userDTO.getAge(),
            userDTO.getHeight(),
            userDTO.getWeight()
        );
        return ResponseEntity.status(201).body(userMapper.toDTO(user));
    }

    @PutMapping("/{username}/profile")
    public ResponseEntity<UserResponseDTO> updateUserProfile(@PathVariable String username, @RequestBody UserProfileUpdateDTO userDTO) {
        Optional<User> existing = userService.getUserByName(username);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = existing.get();
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) user.setEmail(userDTO.getEmail());
        if (userDTO.getAge() != null && userDTO.getAge() > 0) user.setAge(userDTO.getAge());
        if (userDTO.getHeight() != null && userDTO.getHeight() > 0) user.setHeight(userDTO.getHeight());
        if (userDTO.getWeight() != null && userDTO.getWeight() > 0) user.setWeight(userDTO.getWeight());
        if (userDTO.getProfileImageUri() != null) user.setProfileImageUri(userDTO.getProfileImageUri());
        user.setPhone(userDTO.getPhone());
        // Actualizar posición si viene en el DTO
        if (userDTO.getPosition() != null && !userDTO.getPosition().isEmpty()) {
            try { user.setPosition(PlayerPosition.valueOf(userDTO.getPosition())); }
            catch (Exception ignored) {}
        }
        if (userDTO.getSecondaryPosition() != null && !userDTO.getSecondaryPosition().isEmpty()) {
            try { user.setSecondaryPosition(PlayerPosition.valueOf(userDTO.getSecondaryPosition())); }
            catch (Exception ignored) {}
        } else if (userDTO.getSecondaryPosition() != null) {
            user.setSecondaryPosition(null);
        }
        return ResponseEntity.ok(userMapper.toDTO(userService.updateUserProfile(user)));
    }

    @PatchMapping("/{username}/attributes")
    public ResponseEntity<UserResponseDTO> updateUserAttributes(
            @PathVariable String username,
            @RequestBody UserAttributesDTO userAttributesDTO) {
        User updated = userService.updateUserAttributes(username, userAttributesDTO.getAge(), userAttributesDTO.getHeight(), userAttributesDTO.getWeight());
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @PatchMapping("/{username}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(
            @PathVariable String username,
            @RequestBody UserRoleDTO userRoleDTO) {
        User updated = userService.updateUserRole(username, userRoleDTO.getType(), userRoleDTO.getClubName());
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        // Si se está quitando el club (clubName vacío), notificar al usuario via WS
        String newClub = userRoleDTO.getClubName();
        if (newClub == null || newClub.isEmpty()) {
            try {
                org.json.JSONObject wsMsg = new org.json.JSONObject();
                wsMsg.put("type", "club_dissolved");
                wsMsg.put("playerName", username);
                wsMsg.put("teamName", "");
                lineupWsHandler.broadcast(wsMsg.toString());
            } catch (Exception ignored) {}
        }
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @PostMapping("/{username}/join-request")
    public ResponseEntity<UserResponseDTO> sendJoinRequest(
            @PathVariable String username,
            @RequestBody JoinRequestDTO joinRequestDTO) {
        User updated = userService.sendJoinRequest(username, joinRequestDTO.getClubName());
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @DeleteMapping("/{username}/join-request")
    public ResponseEntity<UserResponseDTO> cancelJoinRequest(@PathVariable String username) {
        User updated = userService.cancelJoinRequest(username);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        // Notificar al usuario via WS con tipo "club_expelled" (distinto de "team_change")
        // para que el cliente no confunda esta notificación con el flujo de aceptación
        // que también envía team_change con teamName="".
        try {
            org.json.JSONObject wsMsg = new org.json.JSONObject();
            wsMsg.put("type", "club_expelled");
            wsMsg.put("playerName", username);
            wsMsg.put("teamName", "");
            lineupWsHandler.broadcast(wsMsg.toString());
        } catch (Exception ignored) {}
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @GetMapping("/{username}/pending-request")
    public ResponseEntity<PendingRequestDTO> getPendingRequestClub(@PathVariable String username) {
        return ResponseEntity.ok(new PendingRequestDTO(userService.getPendingRequestClubForUser(username)));
    }

    @GetMapping("/{username}/has-pending-request")
    public ResponseEntity<Boolean> hasPendingRequest(@PathVariable String username) {
        return ResponseEntity.ok(userService.hasAnyPendingRequest(username));
    }

    @GetMapping("/{username}/has-approved-request")
    public ResponseEntity<Boolean> hasApprovedRequest(@PathVariable String username) {
        return ResponseEntity.ok(userService.hasApprovedRequest(username));
    }

    @GetMapping("/{username}/exists")
    public ResponseEntity<Boolean> userExists(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.userExists(username));
    }

    @GetMapping("/email-available")
    public ResponseEntity<Boolean> isEmailAvailable(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.isEmailAvailable(email));
    }

    /**
     * Obtener usuario por nombre
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDTO> getUserByName(@PathVariable String username) {
        Optional<User> user = userService.getUserByName(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(userMapper.toDTO(user.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Obtener jugadores de un equipo
     */
    @GetMapping("/team/{teamName}/players")
    public ResponseEntity<List<UserResponseDTO>> getPlayersByTeam(@PathVariable String teamName) {
        List<UserResponseDTO> players = userService.getPlayersByTeam(teamName).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(players);
    }

    /**
     * Obtener usuarios por club
     */
    @GetMapping("/club/{clubName}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByClub(@PathVariable String clubName) {
        List<UserResponseDTO> users = userService.getUsersByClub(clubName).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Obtener usuarios pendientes de un club
     */
    @GetMapping("/club/{clubName}/pending")
    public ResponseEntity<List<UserResponseDTO>> getPendingUsersByClub(@PathVariable String clubName) {
        List<UserResponseDTO> users = userService.getPendingUsersByClub(clubName).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Actualizar estado de usuario
     */
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable String username,
            @RequestBody UserStatusDTO userStatusDTO) {
        userService.updateUserStatus(username, userStatusDTO.getStatus());
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualizar equipo de usuario
     */
    @PatchMapping("/{username}/team")
    public ResponseEntity<Void> updateUserTeam(
            @PathVariable String username,
            @RequestBody UserTeamDTO userTeamDTO) {
        userService.updateUserTeam(username, userTeamDTO.getTeamName());

        // Broadcast via raw WebSocket (WS 2: cambio de equipo → jugador actualiza su panel)
        try {
            org.json.JSONObject wsMsg = new org.json.JSONObject();
            wsMsg.put("type", "team_change");
            wsMsg.put("playerName", username);
            wsMsg.put("teamName", userTeamDTO.getTeamName() != null ? userTeamDTO.getTeamName() : "");
            lineupWsHandler.broadcast(wsMsg.toString());
        } catch (Exception ignored) {}

        return ResponseEntity.noContent().build();
    }

    /**
     * Actualizar posición en alineación
     */
    @PatchMapping("/{username}/lineup-position")
    public ResponseEntity<Void> updateLineupPosition(
            @PathVariable String username,
            @Valid @RequestBody UserLineupPositionDTO lineupPositionDTO) {
        userService.updateUserLineupPosition(username, lineupPositionDTO.getX(), lineupPositionDTO.getY());
        
        // Difundir la actualización por WebSocket
        Optional<User> optionalUser = userService.getUserByName(username);
        if (optionalUser.isPresent()) {
            User updatedUser = optionalUser.get();
            if (updatedUser.getTeam() != null) {
                String teamName = updatedUser.getTeam().getName();
                UserResponseDTO userResponseDTO = userMapper.toDTO(updatedUser);
                messagingTemplate.convertAndSend("/topic/teams/" + teamName + "/lineup", userResponseDTO);
            }
        }
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualizar estadísticas del jugador
     */
    @PatchMapping("/{username}/stats")
    public ResponseEntity<Void> updateStats(
            @PathVariable String username,
            @RequestBody UserStatsDTO userStatsDTO) {

        Optional<User> optionalUser = userService.getUserByName(username);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("El usuario '" + username + "' no existe");
        }

        userService.updateUserStats(username, userStatsDTO.getMatchesPlayed(), userStatsDTO.getMatchesStarted(), userStatsDTO.getGoals(), userStatsDTO.getAssists());

        // 1. Broadcast via STOMP (para clientes que usen STOMP)
        User updatedUser = userService.getUserByName(username).get();
        if (updatedUser.getTeam() != null) {
            String teamName = updatedUser.getTeam().getName();
            messagingTemplate.convertAndSend("/topic/teams/" + teamName + "/stats", userMapper.toDTO(updatedUser));
        }

        // 2. Broadcast via raw WebSocket (para clientes Android)
        try {
            org.json.JSONObject wsMsg = new org.json.JSONObject();
            wsMsg.put("type", "stats_update");
            wsMsg.put("playerName", username);
            wsMsg.put("teamName", updatedUser.getTeamName() != null ? updatedUser.getTeamName() : "");
            wsMsg.put("matchesPlayed", userStatsDTO.getMatchesPlayed());
            wsMsg.put("matchesStarted", userStatsDTO.getMatchesStarted());
            wsMsg.put("goals", userStatsDTO.getGoals());
            wsMsg.put("assists", userStatsDTO.getAssists());
            lineupWsHandler.broadcast(wsMsg.toString());
        } catch (Exception ignored) {}

        return ResponseEntity.noContent().build();
    }

    /**
     * Actualizar posición del jugador (posición principal y alternativa)
     */
    @PatchMapping("/{username}/position")
    public ResponseEntity<UserResponseDTO> updateUserPosition(
            @PathVariable String username,
            @RequestBody UserPositionDTO positionDTO) {
        User updated = userService.updateUserPosition(username,
                positionDTO.getPosition(), positionDTO.getSecondaryPosition());
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @PatchMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String username,
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        boolean changed = userService.changePassword(username, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
        return changed ? ResponseEntity.noContent().build() : ResponseEntity.status(400).build();
    }

    /**
     * Obtener todos los nombres de clubs
     */
    @GetMapping("/clubs/all")
    public ResponseEntity<List<String>> getAllClubNames() {
        return ResponseEntity.ok(userService.getAllClubNames());
    }

    /**
     * Salir del club (jugador/entrenador deja el club voluntariamente)
     */
    @DeleteMapping("/{username}/leave-club")
    public ResponseEntity<Void> leaveClub(@PathVariable String username) {
        Optional<User> opt = userService.getUserByName(username);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        userService.leaveClub(username);
        // Broadcast team_change con equipo vacío
        try {
            org.json.JSONObject wsMsg = new org.json.JSONObject();
            wsMsg.put("type", "team_change");
            wsMsg.put("playerName", username);
            wsMsg.put("teamName", "");
            lineupWsHandler.broadcast(wsMsg.toString());
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    /**
     * Eliminar usuario (con cascade si es CLUB)
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        Optional<User> opt = userService.getUserByName(username);
        if (opt.isPresent() && opt.get().getType() == UserType.CLUB) {
            String clubName = opt.get().getName();
            // Notificar a todos los miembros antes de borrar
            try {
                java.util.List<com.backend.gesteam.entity.User> members =
                        userService.getUsersByClub(clubName);
                for (com.backend.gesteam.entity.User member : members) {
                    org.json.JSONObject wsMsg = new org.json.JSONObject();
                    wsMsg.put("type", "club_dissolved");
                    wsMsg.put("playerName", member.getName());
                    wsMsg.put("teamName", "");
                    lineupWsHandler.broadcast(wsMsg.toString());
                }
            } catch (Exception ignored) {}
            // Desvincular miembros y equipos
            userService.orphanClubMembers(clubName);
            teamService.deleteTeamsByClub(clubName);
        }
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    // ── Código de invitación ──────────────────────────────────────────────────

    @PostMapping("/{username}/invite-code")
    public ResponseEntity<Map<String, Object>> generateInviteCode(@PathVariable String username) {
        Optional<User> opt = userService.getUserByName(username);
        if (opt.isEmpty() || opt.get().getType() != UserType.CLUB) {
            return ResponseEntity.badRequest().build();
        }
        String code = generateInviteCodeValue();
        long expiry = System.currentTimeMillis() + INVITE_TTL_MS;
        inviteCodes.put(username, new String[]{code, String.valueOf(expiry)});
        java.util.HashMap<String, Object> resp = new java.util.HashMap<>();
        resp.put("code", code);
        resp.put("expiresAt", expiry);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{username}/invite-code")
    public ResponseEntity<Map<String, Object>> getInviteCode(@PathVariable String username) {
        String[] entry = inviteCodes.get(username);
        long now = System.currentTimeMillis();
        java.util.HashMap<String, Object> resp = new java.util.HashMap<>();
        if (entry == null || Long.parseLong(entry[1]) < now) {
            resp.put("code", "");
            resp.put("expiresAt", 0L);
        } else {
            resp.put("code", entry[0]);
            resp.put("expiresAt", Long.parseLong(entry[1]));
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{username}/join-with-code")
    public ResponseEntity<Map<String, String>> joinWithCode(
            @PathVariable String username,
            @RequestBody Map<String, String> body) {
        String code = body != null ? body.get("code") : null;
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        for (Map.Entry<String, String[]> entry : inviteCodes.entrySet()) {
            String[] data = entry.getValue();
            if (data[0].equalsIgnoreCase(code) && Long.parseLong(data[1]) > System.currentTimeMillis()) {
                String clubUsername = entry.getKey();
                // Crear solicitud PENDIENTE en vez de aprobar directamente
                userService.sendJoinRequest(username, clubUsername);
                return ResponseEntity.ok(Map.of("clubName", clubUsername, "status", "PENDING"));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Código inválido o expirado"));
    }
}
