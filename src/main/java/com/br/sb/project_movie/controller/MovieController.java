package com.br.sb.project_movie.controller;

import com.br.sb.project_movie.annotations.BindJson;
import com.br.sb.project_movie.dto.MovieDto;
import com.br.sb.project_movie.dto.MovieOutputDto;
import com.br.sb.project_movie.mapper.MovieMapper;
import com.br.sb.project_movie.model.Movie;
import com.br.sb.project_movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
@Slf4j
public class MovieController implements GenericController {

    private final MovieService movieService;

    private final MovieMapper movieMapper;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMovie(@BindJson(value = "json") @Valid MovieDto json,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "trailer", required = false) MultipartFile trailer) {
        try {
            Movie movie = movieMapper.toModel(json);

            if (image != null) {
                movie.setImage(image.getBytes());
            }
            if (trailer != null) {
                movie.setTrailer(trailer.getBytes());
            }
            MovieDto savedMovie = movieMapper.toDto(movieService.saveMovie(movie));

            if (savedMovie == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error creating movie: Movie could not be saved.");
            }
            HttpHeaders headers = gerarHaderLoccation("/movies/" + savedMovie.id());
            return new ResponseEntity<>(savedMovie, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating movie: " + e.
getMessage());
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMovie(@BindJson(value = "json") @Valid MovieDto json,
                                         @RequestPart(value = "image", required = false) MultipartFile image,
                                         @RequestPart(value = "trailer", required = false) MultipartFile trailer) {
        try {
            if (json == null || json.id() == null) {
                return ResponseEntity.badRequest().body("Invalid movie data");
            }

            Movie byId = movieService.findById(json.id());
            if (byId == null) {
                return ResponseEntity.notFound().build();
            }

            movieMapper.partialUpdate(json, byId); // Update byId directly
            Movie movie1 = byId; // Use the updated byId

            if (image != null && !image.isEmpty()) {
                movie1.setImage(image.getBytes());
            }
            if (trailer != null && !trailer.isEmpty()) {
                movie1.setTrailer(trailer.getBytes());
            }

            Movie updatedMovie = movieService.update(movie1);
            MovieDto savedMovie = movieMapper.toDto(updatedMovie);
            HttpHeaders headers = gerarHaderLoccation("/movies/" + savedMovie.id());
            return new ResponseEntity<>(savedMovie, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating movie: " + e.getMessage());
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Object> findMovieById(@PathVariable("id") String id) {
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        id = id.trim();
        UUID uuid = UUID.fromString(id);
        Movie movie = movieService.findById(uuid);
        MovieOutputDto movieOutputDto = movieMapper.OUTPUT_DTO(movie);
        return ResponseEntity.ok(movieOutputDto);
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
