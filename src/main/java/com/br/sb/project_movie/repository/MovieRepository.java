package com.br.sb.project_movie.repository;

import com.br.sb.project_movie.model.Movie;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    boolean existsByTitle(String title);

    boolean existsById(UUID id);

    boolean existsByTitleAndIdNot(String title, UUID id);

    @Query("SELECT m FROM Movie m JOIN FETCH m.cast WHERE m.id = ?1")
    Optional<Movie> findById(UUID uuid);

    Optional<Movie> findByTitle(String title);

    @Query("SELECT m FROM Movie m JOIN FETCH m.cast")
    List<Movie> findAll();
}
