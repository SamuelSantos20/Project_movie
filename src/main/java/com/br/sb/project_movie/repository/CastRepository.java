package com.br.sb.project_movie.repository;
import com.br.sb.project_movie.model.Cast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CastRepository extends JpaRepository<Cast, UUID> {

    boolean existsByName(String castName);

    Optional<Cast> findByName(String name);

    boolean existsById(UUID id);
}
