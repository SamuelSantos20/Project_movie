package com.br.sb.project_movie.controller;

import com.br.sb.project_movie.annotations.BindJson;
import com.br.sb.project_movie.config.RabbitConfig;
import com.br.sb.project_movie.dto.AsyncMovieAnalysisMessage;
import com.br.sb.project_movie.dto.MovieDto;
import com.br.sb.project_movie.dto.MovieOutput;
import com.br.sb.project_movie.dto.MovieOutputDto;
import com.br.sb.project_movie.mapper.MovieMapper;
import com.br.sb.project_movie.model.Movie;
import com.br.sb.project_movie.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/movies/")
@RequiredArgsConstructor
public class MovieController implements GenericController {

    private final OpenCVService openCVService;
    private final MovieService movieService;
    private final MovieMapper movieMapper;
    private final ObjectMapper objectMapper;
    private final AnalysisStatusService analysisStatusService;
    private final RabbitTemplate rabbitTemplate;
    private final MovieCacheService movieCacheService;
    private final RedisTemplate redisTemplate;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMovie(
            @BindJson("json") @Valid MovieDto json,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "trailer", required = false) MultipartFile trailer) {
        try {
            log.info("Criando filme: {}", json.title());
            Movie movie = movieMapper.toModel(json);

            // Verifica se imagem foi enviada
            if (image == null || image.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Imagem é obrigatória para detecção facial."
                ));
            }

            if (trailer == null || trailer.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Trailer é obrigatório para processamento."
                ));
            }

            try {
                UUID uuid = UUID.randomUUID();
                Movie movieWithId = movieMapper.toModel(json);
                movieWithId.setTrailer(trailer.getBytes());

                // coloca no cache temporário
                movieCacheService.cacheMovie(uuid, movieWithId);

                // Enviar para RabbitMQ: json + image + trailer + uuid
                AsyncMovieAnalysisMessage message = new AsyncMovieAnalysisMessage(uuid, image.getBytes());
                rabbitTemplate.convertAndSend(RabbitConfig.VIDEO_QUEUE, message);
                String cacheKey = "movie:" + uuid;
                redisTemplate.opsForValue().set(cacheKey, movieWithId);

                //Retorna o ID para acompanhamento do status
                analysisStatusService.setStatus(uuid.toString(), StatusProcessamento.EM_PROCESSAMENTO.getDescricao());
                return ResponseEntity.accepted().body(Map.of(
                        "message", "Processamento iniciado. Use o ID para acompanhar.",
                        "id", uuid, "Utilize esse identificador para limpar o cache", cacheKey
                ));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }


        }catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStatus(@PathVariable String id) {
        String status = analysisStatusService.getStatus(id);
        if ("ID não encontrado".equals(status)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Status para o ID informado não encontrado."));
        }
        return ResponseEntity.ok(Map.of("id", id, "status", status));
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
            // Use the updated byId

            if (image != null && !image.isEmpty()) {
                byId.setImage(image.getBytes());
            }
            if (trailer != null && !trailer.isEmpty()) {
                byId.setTrailer(trailer.getBytes());
            }

            Movie updatedMovie = movieService.update(byId);
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
    public ResponseEntity<?> findAllMovies() {

        List<?> rawMovies = movieService.findAll(); // Supondo que retorne List<Object>

        if (rawMovies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<MovieOutput> movieDtos = rawMovies.stream()
                .map(obj -> objectMapper.convertValue(obj, Movie.class))
                .map(movieMapper::toOutput)
                .collect(Collectors.toList());

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
