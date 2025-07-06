package com.br.sb.project_movie.dto;

import java.util.UUID;

public record MovieOutput(UUID id,
                          String title,
                          String description,
                          String genre,
                          String director,
                          Double rating,
                          String duration,
                          String classification,
                          String image,
                          String trailer) {
}
