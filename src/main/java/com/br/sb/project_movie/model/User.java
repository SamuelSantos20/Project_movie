package com.br.sb.project_movie.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name",unique = true, nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "email", unique = true, nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String email;
    @Column(name = "password", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String password;
    @Column(name = "role", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private String role;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
