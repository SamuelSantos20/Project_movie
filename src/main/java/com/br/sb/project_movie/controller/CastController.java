package com.br.sb.project_movie.controller;

import com.br.sb.project_movie.dto.CastDto;
import com.br.sb.project_movie.mapper.CastMapper;
import com.br.sb.project_movie.model.Cast;
import com.br.sb.project_movie.service.CastService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/casts")
@Slf4j
public class CastController {

    private final CastService castService;

    private final CastMapper castMapper;

    @PostMapping("/create")
    public ResponseEntity<Object> saveCast(@RequestBody @Valid CastDto castDto) {
        if (castDto == null) {
            return ResponseEntity.badRequest().body("Cast cannot be null");
        }

        var cast = castMapper.toModel(castDto);
        var savedCast = castService.saveCast(cast);

        if (savedCast == null) {
            return ResponseEntity.status(500).body("Error creating cast: Cast could not be saved.");
        }

        var savedCastDto = castMapper.toDto(savedCast);
        return ResponseEntity.status(201).body(savedCastDto);

    }

    @PutMapping("/update")
    public  ResponseEntity<?>  updateCast(@RequestBody @Valid CastDto castDto) {
        if (castDto == null || castDto.id() == null) {
            throw new IllegalArgumentException("Cast or Cast ID cannot be null");
        }
        Cast model = castMapper.toModel(castDto);

        var updatedCast = castService.update(model);
        if (updatedCast == null) {
            throw new IllegalArgumentException("Error updating cast: Cast could not be updated.");
        }

        return ResponseEntity.ok(castMapper.toDto(updatedCast));


    }


    @GetMapping("/find/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") UUID id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Cast ID cannot be null");
        }
        var cast = castService.findById(id);
        if (cast.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(castMapper.toDto(cast.get()));
    }

    @GetMapping("/find")
    public ResponseEntity<Object> findAllCast() {
        var casts = castService.findAllCast();
        if (!casts.iterator().hasNext()) {
            return ResponseEntity.noContent().build();
        }

        var castDtos = casts.stream()
                .map(castMapper::toDto)
                .toList();

        return ResponseEntity.ok(castDtos);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCast(@PathVariable("id") UUID id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Cast ID cannot be null");
        }
        castService.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
