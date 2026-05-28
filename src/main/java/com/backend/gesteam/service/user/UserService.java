package com.backend.gesteam.service.user;

import com.backend.gesteam.entity.User;
import com.backend.gesteam.enums.PlayerPosition;
import com.backend.gesteam.enums.UserType;
import com.backend.gesteam.exceptions.GesteamBusinessException;
import com.backend.gesteam.repository.TeamJpaRepository;
import com.backend.gesteam.repository.impl.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeamJpaRepository teamJpaRepository;

    /**
     * Elimina a un usuario de TODOS sus equipos de forma robusta:
     *  - Elimina de team_members (lado propietario) con save explícito por equipo
     *  - Limpia coach_id FK si era entrenador de algún equipo
     *  - Limpia los campos legacy teamName y team_id del usuario
     *
     * Usa teamJpaRepository.findTeamsByPlayerName (JPQL con LEFT JOIN) para encontrar
     * los equipos, y guarda cada Team explícitamente para garantizar la persistencia
     * en team_members (no se basa en dirty-checking de Hibernate sobre la relación inversa).
     */
    private void clearAllTeamMemberships(String username) {
        // 1. Quitar de team_members (owning side) + coach_id en cada equipo que lo tenga
        java.util.List<com.backend.gesteam.entity.Team> teams =
                teamJpaRepository.findTeamsByPlayerName(username);
        for (com.backend.gesteam.entity.Team t : teams) {
            boolean dirty = false;
            if (t.getPlayers() != null) {
                dirty = t.getPlayers().removeIf(p -> username.equals(p.getName())) || dirty;
            }
            if (t.getCoach() != null && username.equals(t.getCoach().getName())) {
                t.setCoach(null);
                dirty = true;
            }
            if (dirty) teamJpaRepository.save(t);
        }
        // 2. Limpiar FK legacy team_id y campo teamName en el usuario
        Optional<User> opt = userRepository.getUserByName(username);
        if (opt.isPresent()) {
            User u = opt.get();
            u.setTeamName(null);
            u.setTeam(null);
            userRepository.updateUser(u);
        }
    }

    /**
     * Crear un nuevo usuario
     */
    public User createUser(String name, UserType type, String teamName, String clubName, String status, String password) {
        if (userRepository.getUserByName(name).isPresent()) {
            throw new GesteamBusinessException("Ya existe un usuario con el nombre '" + name + "'");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(name, type, teamName, clubName, status, hashedPassword);
        return userRepository.addUser(user);
    }

    /**
     * Crear usuario con información completa
     */
    public User createUserWithDetails(String name, UserType type, String teamName, String clubName,
                                      String status, String password, String email, int age,
                                      float height, float weight) {
        if (userRepository.getUserByName(name).isPresent()) {
            throw new GesteamBusinessException("Ya existe un usuario con el nombre '" + name + "'");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(name, type, teamName, clubName, status, hashedPassword, email, age, height, weight);
        return userRepository.addUser(user);
    }

    /**
     * Autenticar usuario
     */
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.getUserByName(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public User sendJoinRequest(String username, String clubName) {
        Optional<User> user = userRepository.getUserByName(username);
        if (user.isEmpty()) {
            return null;
        }

        User u = user.get();
        u.setClubName(clubName);
        u.setStatus("PENDING");
        return userRepository.updateUser(u);
    }

    public User cancelJoinRequest(String username) {
        Optional<User> user = userRepository.getUserByName(username);
        if (user.isEmpty()) {
            return null;
        }

        User u = user.get();
        u.setClubName(null);
        u.setStatus(null);  // ni PENDING ni APPROVED — el usuario vuelve a estar sin club
        User saved = userRepository.updateUser(u);

        // Limpiar todas las membresías de equipo (team_members owning side + legacy FKs)
        clearAllTeamMemberships(username);

        return saved;
    }

    public boolean hasPendingRequest(String username, String clubName) {
        return userRepository.getUserByName(username)
                .map(user -> "PENDING".equalsIgnoreCase(user.getStatus())
                        && clubName != null
                        && clubName.equals(user.getClubName()))
                .orElse(false);
    }

    public boolean hasAnyPendingRequest(String username) {
        return userRepository.getUserByName(username)
                .map(user -> "PENDING".equalsIgnoreCase(user.getStatus()))
                .orElse(false);
    }

    public String getPendingRequestClubForUser(String username) {
        return userRepository.getUserByName(username)
                .filter(user -> "PENDING".equalsIgnoreCase(user.getStatus()))
                .map(User::getClubName)
                .orElse(null);
    }

    public boolean hasApprovedRequest(String username) {
        return userRepository.getUserByName(username)
                .map(user -> "APPROVED".equalsIgnoreCase(user.getStatus()))
                .orElse(false);
    }

    public User updateUserRole(String username, UserType type, String clubName) {
        Optional<User> user = userRepository.getUserByName(username);
        if (user.isEmpty()) {
            return null;
        }

        User u = user.get();
        u.setType(type);
        u.setClubName(clubName);
        return userRepository.updateUser(u);
    }

    /**
     * Obtener usuario por nombre
     */
    public Optional<User> getUserByName(String username) {
        return userRepository.getUserByName(username);
    }

    /**
     * Obtener todos los usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    /**
     * Obtener jugadores de un equipo
     */
    public List<User> getPlayersByTeam(String teamName) {
        return userRepository.getPlayersByTeamName(teamName);
    }

    /**
     * Obtener usuarios por club
     */
    public List<User> getUsersByClub(String clubName) {
        return userRepository.getUsersByClubName(clubName);
    }

    /**
     * Obtener usuarios pendientes de aprobación en un club
     */
    public List<User> getPendingUsersByClub(String clubName) {
        return userRepository.getPendingUsersByClub(clubName);
    }

    /**
     * Actualizar estado de usuario (PENDING, APPROVED, REJECTED)
     */
    public void updateUserStatus(String username, String status) {
        userRepository.updateUserStatus(username, status);
    }

    /**
     * Actualizar equipo de usuario.
     * Si teamName es vacío/null → limpia todas las membresías de equipo (team_members,
     * coach_id, teamName, team_id) para que el jugador no se auto-asigne a un equipo
     * antiguo al ser re-aceptado en el club.
     */
    public void updateUserTeam(String username, String teamName) {
        if (teamName == null || teamName.isEmpty()) {
            clearAllTeamMemberships(username);
        } else {
            userRepository.updateUserTeam(username, teamName);
        }
    }

    /**
     * Actualizar club de usuario
     */
    public void updateUserClub(String username, String clubName) {
        userRepository.updateUserClub(username, clubName);
    }

    /**
     * Actualizar posición en alineación
     */
    public void updateUserLineupPosition(String username, float x, float y) {
        userRepository.updateUserLineupPosition(username, x, y);
    }

    /**
     * Actualizar estadísticas del jugador
     */
    public void updateUserStats(String username, int matchesPlayed, int matchesStarted, int goals, int assists) {
        userRepository.updateUserStats(username, matchesPlayed, matchesStarted, goals, assists);
    }

    /**
     * Actualizar atributos físicos del usuario
     */
    public User updateUserAttributes(String username, int age, float height, float weight) {
        Optional<User> user = userRepository.getUserByName(username);
        if (user.isEmpty()) {
            return null;
        }

        User u = user.get();
        u.setAge(age);
        u.setHeight(height);
        u.setWeight(weight);
        return userRepository.updateUser(u);
    }

    /**
     * Cambiar tipo de usuario (PLAYER, COACH, ADMIN, CLUB, USER)
     */
    public void updateUserType(String username, UserType type) {
        userRepository.updateUserType(username, type.name());
    }

    /**
     * Actualizar perfil completo del usuario
     */
    public User updateUserProfile(User user) {
        return userRepository.updateUser(user);
    }

    /**
     * Salir del club voluntariamente (jugador/entrenador)
     */
    public void leaveClub(String username) {
        Optional<User> opt = userRepository.getUserByName(username);
        if (opt.isPresent()) {
            User u = opt.get();
            u.setClubName(null);
            u.setTeamName(null);
            u.setTeam(null);
            u.setLineupX(-1f);
            u.setLineupY(-1f);
            u.setStatus("APPROVED");
            // Limpiar membresías ManyToMany
            try {
                java.util.List<com.backend.gesteam.entity.Team> memberTeams = u.getTeams();
                if (memberTeams != null) {
                    for (com.backend.gesteam.entity.Team t : new java.util.ArrayList<>(memberTeams)) {
                        t.getPlayers().removeIf(p -> username.equals(p.getName()));
                    }
                    memberTeams.clear();
                }
            } catch (Exception ignored) {}
            userRepository.updateUser(u);
        }
    }

    /**
     * Eliminar usuario (con cascade si es CLUB)
     */
    public void deleteUser(String username) {
        userRepository.deleteUser(username);
    }

    /**
     * Orfenar a todos los miembros de un club (llamado antes de borrar la cuenta de club)
     */
    public void orphanClubMembers(String clubName) {
        List<User> members = userRepository.getUsersByClubName(clubName);
        for (User member : members) {
            member.setClubName(null);
            member.setTeamName(null);
            member.setTeam(null);
            member.setLineupX(-1f);
            member.setLineupY(-1f);
            member.setStatus("APPROVED");
            userRepository.updateUser(member);
        }
    }

    /**
     * Obtener todos los nombres de clubs únicos
     */
    public List<String> getAllClubNames() {
        return userRepository.getAllClubNames();
    }

    /**
     * Obtener usuarios por estado
     */
    public List<User> getUsersByStatus(String status) {
        return userRepository.getUsersByStatus(status);
    }

    /**
     * Verificar si usuario existe
     */
    public boolean userExists(String username) {
        return userRepository.getUserByName(username).isPresent();
    }

    /**
     * Verificar si email está disponible
     */
    public boolean isEmailAvailable(String email) {
        return userRepository.getAllUsers().stream()
                .noneMatch(u -> email.equals(u.getEmail()));
    }

    /**
     * Actualizar posición del jugador (PORTERO, DEFENSA, MEDIO, ATACANTE)
     */
    public User updateUserPosition(String username, String position, String secondaryPosition) {
        Optional<User> user = userRepository.getUserByName(username);
        if (user.isEmpty()) return null;
        User u = user.get();
        if (position != null && !position.isEmpty()) {
            try { u.setPosition(PlayerPosition.valueOf(position)); }
            catch (Exception ignored) { u.setPosition(null); }
        } else {
            u.setPosition(null);
        }
        if (secondaryPosition != null && !secondaryPosition.isEmpty()) {
            try { u.setSecondaryPosition(PlayerPosition.valueOf(secondaryPosition)); }
            catch (Exception ignored) { u.setSecondaryPosition(null); }
        } else {
            u.setSecondaryPosition(null);
        }
        return userRepository.updateUser(u);
    }

    /**
     * Cambiar contraseña de usuario
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.getUserByName(username);
        if (user.isPresent() && passwordEncoder.matches(oldPassword, user.get().getPassword())) {
            User u = user.get();
            u.setPassword(passwordEncoder.encode(newPassword));
            userRepository.updateUser(u);
            return true;
        }
        return false;
    }
}
