package com.br.sb.project_movie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CastDto(
        UUID id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "Birth date cannot be null")
        LocalDate birthDate,
        @NotBlank(message = "Nationality cannot be blank")
        String nationality
) {
}
