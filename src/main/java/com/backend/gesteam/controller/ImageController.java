package com.backend.gesteam.controller;

import com.backend.gesteam.entity.Team;
import com.backend.gesteam.entity.User;
import com.backend.gesteam.service.team.TeamService;
import com.backend.gesteam.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class ImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @PostMapping("/api/users/{username}/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @PathVariable String username,
            @RequestParam("image") MultipartFile file,
            HttpServletRequest request) {

        if (file.isEmpty()) return ResponseEntity.badRequest().build();
        try {
            byte[] imageBytes = file.getBytes();
            Optional<User> userOpt = userService.getUserByName(username);
            if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
            User user = userOpt.get();
            user.setProfileImage(imageBytes);
            // El timestamp en la URL fuerza a Glide a recargar la imagen tras cada subida
            String imageUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + "/api/users/" + username
                    + "/profile-image?v=" + System.currentTimeMillis();
            user.setProfileImageUri(imageUrl);
            userService.updateUserProfile(user);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/users/{username}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable String username) {
        Optional<User> userOpt = userService.getUserByName(username);
        if (userOpt.isEmpty() || userOpt.get().getProfileImage() == null
                || userOpt.get().getProfileImage().length == 0)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                .body(userOpt.get().getProfileImage());
    }

    @PostMapping("/api/teams/{teamName}/profile-image")
    public ResponseEntity<Map<String, String>> uploadTeamProfileImage(
            @PathVariable String teamName,
            @RequestParam("image") MultipartFile file,
            HttpServletRequest request) {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();
        try {
            byte[] imageBytes = file.getBytes();
            Optional<Team> teamOpt = teamService.getTeamByName(teamName);
            if (teamOpt.isEmpty()) return ResponseEntity.notFound().build();
            Team team = teamOpt.get();
            team.setProfileImage(imageBytes);
            String imageUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + "/api/teams/" + teamName
                    + "/profile-image?v=" + System.currentTimeMillis();
            team.setProfileImageUri(imageUrl);
            teamService.updateTeam(team);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/teams/{teamName}/profile-image")
    public ResponseEntity<byte[]> getTeamProfileImage(@PathVariable String teamName) {
        Optional<Team> teamOpt = teamService.getTeamByName(teamName);
        if (teamOpt.isEmpty() || teamOpt.get().getProfileImage() == null
                || teamOpt.get().getProfileImage().length == 0)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                .body(teamOpt.get().getProfileImage());
    }
}
