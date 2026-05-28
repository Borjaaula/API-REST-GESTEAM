package com.backend.gesteam.repository;

import com.backend.gesteam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    List<User> findByTeamNameAndType(String teamName, String type);
    List<User> findByClubName(String clubName);
    List<User> findByStatus(String status);
}
