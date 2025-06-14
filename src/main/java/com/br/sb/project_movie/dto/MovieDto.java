package com.br.sb.project_movie.dto;

import com.br.sb.project_movie.model.Cast;
import com.br.sb.project_movie.model.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MovieDto(UUID id,
                       @NotBlank(message = "Title cannot be blank")
                       String title,
                       @NotBlank(message = "Director cannot be blank")
                       String director,
                       @NotNull(message = "Genre cannot be null")
                       Genero genre,
                       @NotNull(message = "Release date cannot be null")
                       LocalDate releaseDate,
                       @NotBlank(message = "Description cannot be blank")
                       String description,
                       @NotNull(message = "Rating cannot be null")
                       Double rating,
                       @NotNull(message = "Cast cannot be null")
                       List<Cast> cast,
                       @NotNull(message = "Duration cannot be null")
                       Duration duration,
                       byte[] image,
                       byte[] trailer,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt
) {
}
