package com.backend.gesteam.config;

import com.backend.gesteam.entity.Team;
import com.backend.gesteam.entity.User;
import com.backend.gesteam.enums.UserType;
import com.backend.gesteam.repository.TeamJpaRepository;
import com.backend.gesteam.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Inicializa / corrige los datos de prueba en cada arranque:
 *  - Actualiza contraseñas en texto plano a BCrypt
 *  - Crea usuarios que falten
 *  - Asegura que admin y el club Manchester City son entidades separadas
 */
@Component
public class TestDataInitializer implements CommandLineRunner {

    @Autowired private UserJpaRepository userRepo;
    @Autowired private TeamJpaRepository teamRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // ── Presidente del club ────────────────────────────────────────────
        ensureUser("Manchester City", UserType.CLUB,   null,              "Manchester City",  "city",  "club@manchestercity.com");

        // ── Entrenador ─────────────────────────────────────────────────────
        ensureUser("Pep Guardiola",   UserType.COACH, "Manchester City", "Manchester City",  "pep",   "pep@manchestercity.com");

        // ── Jugadores ──────────────────────────────────────────────────────
        ensurePlayer("Erling Haaland",  38, 34, 36, 8,  1.95f, 88f, 23);
        ensurePlayer("Kevin De Bruyne", 31, 29,  8, 18, 1.81f, 70f, 32);
        ensurePlayer("Ederson",         36, 36,  0,  0, 1.88f, 86f, 30);
        ensurePlayer("Kyle Walker",     34, 34,  1,  4, 1.83f, 83f, 33);
        ensurePlayer("Ruben Dias",      33, 33,  2,  1, 1.87f, 82f, 26);
        ensurePlayer("John Stones",     28, 27,  2,  2, 1.88f, 82f, 29);
        ensurePlayer("Rodri",           35, 35,  8,  7, 1.91f, 82f, 27);
        ensurePlayer("Bernardo Silva",  37, 35,  6,  8, 1.73f, 64f, 29);
        ensurePlayer("Phil Foden",      34, 26, 10,  7, 1.71f, 70f, 23);
        ensurePlayer("Jack Grealish",   32, 22,  3,  5, 1.75f, 77f, 28);

        // ── Jugador libre (sin club) para probar solicitudes ───────────────
        ensureUser("jugador_libre", UserType.USER, null, null, "libre", "libre@example.com");

        // ── Asegurar que el equipo Manchester City existe y tiene coach ─────
        ensureTeam();
    }

    private void ensureUser(String name, UserType type, String teamName,
                            String clubName, String password, String email) {
        Optional<User> opt = userRepo.findByName(name);
        if (opt.isPresent()) {
            User u = opt.get();
            u.setType(type);
            if (clubName != null) u.setClubName(clubName);
            // Siempre re-hashear al arranque: garantiza que la contraseña de prueba sea correcta
            u.setPassword(passwordEncoder.encode(password));
            userRepo.save(u);
        } else {
            User u = new User(name, type, teamName, clubName, "APPROVED",
                              passwordEncoder.encode(password));
            u.setEmail(email);
            userRepo.save(u);
        }
    }

    private void ensurePlayer(String name, int mp, int ms, int g, int a,
                               float height, float weight, int age) {
        Optional<User> opt = userRepo.findByName(name);
        if (opt.isPresent()) {
            User u = opt.get();
            u.setType(UserType.PLAYER);
            u.setTeamName("Manchester City");
            u.setClubName("Manchester City");
            u.setMatchesPlayed(mp); u.setMatchesStarted(ms);
            u.setGoals(g); u.setAssists(a);
            u.setHeight(height); u.setWeight(weight); u.setAge(age);
            u.setPassword(passwordEncoder.encode("1234")); // siempre sincronizar
            userRepo.save(u);
        } else {
            User u = new User(name, UserType.PLAYER, "Manchester City",
                              "Manchester City", "APPROVED", passwordEncoder.encode("1234"));
            u.setEmail(name.toLowerCase().replace(" ", ".").replace("á", "a")
                          .replace("é", "e").replace("í", "i").replace("ó", "o")
                          .replace("ú", "u") + "@manchestercity.com");
            u.setMatchesPlayed(mp); u.setMatchesStarted(ms);
            u.setGoals(g); u.setAssists(a);
            u.setHeight(height); u.setWeight(weight); u.setAge(age);
            userRepo.save(u);
        }
    }

    private void upgradePlainPassword(User u, String intendedPassword) {
        String pw = u.getPassword();
        boolean isBcrypt = pw != null && (pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$"));
        if (!isBcrypt) {
            u.setPassword(passwordEncoder.encode(intendedPassword));
        }
    }

    private void ensureTeam() {
        Optional<Team> teamOpt = teamRepo.findByName("Manchester City");
        Team team;
        if (teamOpt.isEmpty()) {
            team = new Team("Manchester City", "Manchester City", null);
            team = teamRepo.save(team);
        } else {
            team = teamOpt.get();
        }

        Optional<User> pepOpt = userRepo.findByName("Pep Guardiola");
        if (pepOpt.isPresent()) {
            User pep = pepOpt.get();
            if (team.getCoach() == null) {
                team.setCoach(pep);
            }
            // Asegurar que Pep también esté en team_members para poder quitarlo desde la UI
            boolean pepInMembers = team.getPlayers().stream()
                    .anyMatch(p -> p.getId() != null && p.getId().equals(pep.getId()));
            if (!pepInMembers) {
                team.addPlayer(pep);
            }
            teamRepo.save(team);
        }

        final Team finalTeam = teamRepo.findByName("Manchester City").orElse(team);
        String[] players = {
            "Erling Haaland", "Kevin De Bruyne", "Ederson", "Kyle Walker",
            "Ruben Dias", "John Stones", "Rodri", "Bernardo Silva",
            "Phil Foden", "Jack Grealish", "Julián Álvarez", "Jeremy Doku", "Oscar Bobb"
        };
        boolean teamChanged = false;
        for (String name : players) {
            Optional<User> uOpt = userRepo.findByName(name);
            if (uOpt.isEmpty()) continue;
            User u = uOpt.get();
            // Asegurar FK team_id (compatibilidad con UserRepository.getPlayersByTeamName)
            if (u.getTeam() == null) {
                u.setTeam(finalTeam);
                userRepo.save(u);
            }
            // Asegurar membresía en join table ManyToMany
            boolean alreadyInList = finalTeam.getPlayers().stream()
                    .anyMatch(p -> p.getId() != null && p.getId().equals(u.getId()));
            if (!alreadyInList) {
                finalTeam.addPlayer(u);
                teamChanged = true;
            }
        }
        if (teamChanged) teamRepo.save(finalTeam);
    }
}
