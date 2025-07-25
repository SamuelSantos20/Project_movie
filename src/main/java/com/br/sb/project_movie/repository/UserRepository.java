package com.br.sb.project_movie.repository;

import com.br.sb.project_movie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

}
