package com.br.sb.project_movie.dto;

import com.br.sb.project_movie.model.Movie;
import com.br.sb.project_movie.model.User;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoryDto(UUID id,
                         @NotNull(message = "User cannot be null")
                         User user,
                         @NotNull(message = "Movie cannot be null")
                         Movie movie,
                         @NotNull(message = "Rating cannot be null")
                         Integer rating,
                         @NotNull(message = "View date cannot be null")
                         LocalDateTime viewDate) {
}
