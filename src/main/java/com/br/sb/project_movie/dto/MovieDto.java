package com.br.sb.project_movie.dto;

import com.br.sb.project_movie.model.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record MovieDto(UUID id,
                       @NotBlank
                       String title,
                       @NotBlank
                       String director,
                       @NotNull
                       Genero genre,
                       @NotNull
                       LocalDateTime releaseDate,
                       @NotBlank
                       String description,
                       @NotNull
                       Double rating,
                       @NotNull
                       byte[] image,
                       @NotNull
                       byte[] trailer,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt
) {
}
