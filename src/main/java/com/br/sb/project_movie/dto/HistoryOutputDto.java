package com.br.sb.project_movie.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record HistoryOutputDto(
        UUID id,
        MovieSummary movie,
        UserSummary user,
        Integer rating,
        LocalDateTime viewDate
) {

    public record MovieSummary(
            UUID id,
            String title,
            String director,
            Double rating,
            String genre,
            String description,
            Duration duration,
            byte[] image,
            byte[] trailer,
            LocalDate releaseDate,
            List<CastDto> cast
    ) {}

    public record UserSummary(
            UUID id,
            String name,
            String email
    ) {}
}
