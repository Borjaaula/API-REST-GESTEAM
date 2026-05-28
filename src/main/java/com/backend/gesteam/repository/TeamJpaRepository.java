package com.backend.gesteam.repository;

import com.backend.gesteam.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamJpaRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    List<Team> findByClubName(String clubName);

    List<Team> findByPlayers_Name(String playerName);

    /**
     * Devuelve los equipos en los que aparece un usuario (por nombre), buscando en:
     *  - team_members (ManyToMany players) — via EXISTS + JOIN interno (evita ambigüedad de
     *    LEFT JOIN + WHERE + OR que produce filas duplicadas por jugador y evalúa las condiciones
     *    OR por fila en lugar de por equipo, dando resultados incorrectos en Hibernate)
     *  - teams.coach_id FK (usuario asignado como entrenador oficial del equipo)
     *  - users.team_id FK legacy (asignado por el antiguo mecanismo OneToMany)
     *  - users.teamName legacy (campo de texto con el nombre del equipo principal)
     */
    @Query("SELECT DISTINCT t FROM Team t WHERE " +
           "EXISTS (SELECT p FROM Team t2 JOIN t2.players p WHERE t2 = t AND p.name = :playerName) " +
           "OR (t.coach IS NOT NULL AND t.coach.name = :playerName) " +
           "OR EXISTS (SELECT u FROM User u WHERE u.name = :playerName AND u.team = t) " +
           "OR EXISTS (SELECT u FROM User u WHERE u.name = :playerName AND u.teamName = t.name)")
    List<Team> findTeamsByPlayerName(@Param("playerName") String playerName);
}
