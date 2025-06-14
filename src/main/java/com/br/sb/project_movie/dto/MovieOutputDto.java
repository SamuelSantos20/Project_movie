package com.br.sb.project_movie.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MovieOutputDto(
        UUID id,
        String title,
        String description,
        String genre,
        String director,
        int releaseYear,
        double rating
) {
    public record CastSummaryDto(UUID id,
                                 String name,
                                 LocalDate birthDate,
                                 String nationality,
                                 MovieDto movieDto) {
    }

}
