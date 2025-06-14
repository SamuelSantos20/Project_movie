package com.br.sb.project_movie.repository;

import com.br.sb.project_movie.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {

    @Override
    @Query("SELECT h FROM History h WHERE h.id = ?1")
    Optional<History> findById(UUID uuid);

    @Query("SELECT h FROM History h JOIN FETCH h.movie m JOIN FETCH m.cast JOIN FETCH h.user WHERE h.id = :id")
    Optional<History> findByIdWithAll(UUID id);

    @Query("SELECT h FROM History h JOIN FETCH h.movie m JOIN FETCH m.cast JOIN FETCH h.user")
    List<History> findByAllHistory();

}
