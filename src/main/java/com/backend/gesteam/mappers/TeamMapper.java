package com.backend.gesteam.mappers;

import com.backend.gesteam.dto.TeamResponseDTO;
import com.backend.gesteam.dto.UserResponseDTO;
import com.backend.gesteam.entity.Team;
import org.mapstruct.Mapper;

import java.util.List;

import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    default TeamResponseDTO toDTO(Team team) {
        if (team == null) {
            return null;
        }

        // Coaches: incluimos el coach principal como lista (el cliente Android espera "coaches" array)
        List<UserResponseDTO> coaches = team.getCoach() != null
                ? java.util.List.of(USER_MAPPER.toDTO(team.getCoach()))
                : java.util.List.of();

        // Nombre del coach principal para excluirlo de la lista de players y evitar duplicados.
        // Un coach puede estar almacenado tanto en coach_id (FK) como en team_members (ManyToMany),
        // lo que haría que apareciera dos veces en el cliente Android.
        final String coachName = team.getCoach() != null ? team.getCoach().getName() : null;

        List<UserResponseDTO> players = team.getPlayers() == null
                ? List.of()
                : team.getPlayers().stream()
                        .filter(p -> coachName == null || !coachName.equals(p.getName()))
                        .map(USER_MAPPER::toDTO)
                        .toList();

        return new TeamResponseDTO(
                team.getId(),
                team.getName(),
                team.getClubName(),
                team.getCoach() != null ? USER_MAPPER.toDTO(team.getCoach()) : null,
                coaches,
                players,
                team.isLineupVisible(),
                team.getProfileImageUri()
        );
    }
}
