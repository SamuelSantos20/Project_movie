package com.br.sb.project_movie.dto;

import com.br.sb.project_movie.model.Movie;
import com.br.sb.project_movie.model.User;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoryDto(UUID id,
                         @NotNull
                         User user,
                         @NotNull
                         Movie movie,
                         @NotNull
                         Integer rating,
                         @NotNull
                         LocalDateTime viewDate) {
}
