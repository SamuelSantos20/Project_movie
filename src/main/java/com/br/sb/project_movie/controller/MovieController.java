package com.br.sb.project_movie.controller;

import com.br.sb.project_movie.dto.MovieDto;
import com.br.sb.project_movie.mapper.MovieMapper;
import com.br.sb.project_movie.model.Movie;
import com.br.sb.project_movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController implements GenericController {

    private final MovieService movieService;

    private final MovieMapper movieMapper;

    @PostMapping("/create")
    public ResponseEntity<Object> createMovie(@Valid MovieDto movieDto) {
        Movie movie = movieMapper.toModel(movieDto);
        MovieDto savedMovie = movieMapper.toDto(movieService.saveMovie(movie));
        HttpHeaders headers = gerarHaderLoccation("/movies/" + savedMovie.id());
        return new ResponseEntity<>(savedMovie.id(), headers, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateMovie(@Valid MovieDto movieDto) {
        Movie movie = movieMapper.toModel(movieDto);
        MovieDto savedMovie = movieMapper.toDto(movieService.update(movie));
        HttpHeaders headers = gerarHaderLoccation("/movies/" + savedMovie.id());
        return new ResponseEntity<>(savedMovie.id(), headers, HttpStatus.CREATED);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Object> findMovieById(@PathVariable("id") String id) {
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        id = id.trim();
        UUID uuid = UUID.fromString(id);
        Movie movie = movieService.findById(uuid);
        MovieDto movieDto = movieMapper.toDto(movie);
        return ResponseEntity.ok(movieDto);
    }

    @GetMapping("/find")
    public ResponseEntity<Object> findAllMovies() {
        var movies = movieService.findAll();
        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var movieDtos = movies.stream()
                .map(movieMapper::toDto)
                .toList();
        return ResponseEntity.ok(movieDtos);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteMovie(@PathVariable("id") String id) {
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        id = id.trim();
        UUID uuid = UUID.fromString(id);
        movieService.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

}
