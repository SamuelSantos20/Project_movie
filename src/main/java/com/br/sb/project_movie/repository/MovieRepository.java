package com.br.sb.project_movie.repository;

import com.br.sb.project_movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    boolean existsByTitle(String title);

    boolean existsById(UUID id);

    boolean existsByTitleAndIdNot(String title, UUID id);

    Optional<Movie> findById(UUID uuid);
}
