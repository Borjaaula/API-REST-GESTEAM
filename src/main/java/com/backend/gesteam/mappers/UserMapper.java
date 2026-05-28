package com.backend.gesteam.mappers;

import com.backend.gesteam.dto.UserResponseDTO;
import com.backend.gesteam.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "position",
             expression = "java(user.getPosition() != null ? user.getPosition().name() : null)")
    @Mapping(target = "secondaryPosition",
             expression = "java(user.getSecondaryPosition() != null ? user.getSecondaryPosition().name() : null)")
    UserResponseDTO toDTO(User user);
}
