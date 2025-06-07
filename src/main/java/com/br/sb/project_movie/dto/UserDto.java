package com.br.sb.project_movie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(UUID id,
                      @NotBlank
                      String name,
                      @NotBlank
                      @Email
                      String email,
                      @NotBlank
                      String password,
                      @NotBlank
                      String role,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
}
