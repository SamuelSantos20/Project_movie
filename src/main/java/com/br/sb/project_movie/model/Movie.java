package com.br.sb.project_movie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie")
@EntityListeners(AuditingEntityListener.class)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "title", nullable = false, length = 200, columnDefinition = "VARCHAR(200)")
    private String title;
    @Column(name = "director", nullable = false, length = 100, columnDefinition = "TEXT")
    private String director;
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genero genre;
    @Column(name = "release_date", nullable = false, columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime releaseDate;
    @Column(name = "description", nullable = false, length = 1000, columnDefinition = "TEXT")
    private String description;
    @Column(name = "rating", nullable = false, columnDefinition = "DOUBLE")
    private Double rating;
    @Column(name = "image", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] image;
    @Column(name = "trailer", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] trailer;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;



}
