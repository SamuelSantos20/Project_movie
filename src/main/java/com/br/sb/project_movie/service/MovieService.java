package com.br.sb.project_movie.service;

import com.br.sb.project_movie.config.RabbitConfig;
import com.br.sb.project_movie.dto.AsyncMovieAnalysisMessage;
import com.br.sb.project_movie.dto.MovieDto;
import com.br.sb.project_movie.mapper.MovieMapper;
import com.br.sb.project_movie.model.Movie;
import com.br.sb.project_movie.repository.MovieRepository;
import com.br.sb.project_movie.validation.MovieValidation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, readOnly = false)
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    private final MovieValidation movieValidation;

    private final MovieMapper movieMapper;

    private final ConcurrentMap<UUID, Movie> tempMovieCache = new ConcurrentHashMap<>();

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @CacheEvict(value = "movies", allEntries = true)
    @SneakyThrows
    public Movie saveMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }
        movieValidation.validate(movie);
        if (movieRepository.existsByTitleAndIdNot(movie.getTitle(), movie.getId())) {
            throw new IllegalArgumentException("Movie with title '" + movie.getTitle() + "' already exists");
        }
        //AnalyzeVideo(movie);
        Movie savedMovie = entityManager.merge(movie);

        return savedMovie;
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "movies")
    public Movie findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Movie ID cannot be null");
        }
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Movie findByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be null or blank");
        }
        return movieRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with title: " + title));
    }

    public void deleteById(UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new IllegalArgumentException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }


    public Movie update(Movie movie) {
        if (movie == null || movie.getId() == null) {
            throw new IllegalArgumentException("Movie or Movie ID cannot be null");
        }
        movieValidation.validate(movie);


        if (!movieRepository.existsById(movie.getId())) {
            throw new IllegalArgumentException("Movie not found with id: " + movie.getId());
        }

        MovieDto dto = movieMapper.toDto(movie);

        Movie movie1 = movieRepository.findById(movie.getId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + movie.getId()));

        movieMapper.partialUpdate(dto, movie1);


        return movieRepository.save(movie);
    }

    @Transactional(readOnly = true)
    public boolean existsByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be null or blank");
        }
        return movieRepository.existsByTitle(title);
    }

    public void sendImageForFaceDetection(UUID movieId, byte[] imageBytes, Movie movie) {
        AsyncMovieAnalysisMessage message = new AsyncMovieAnalysisMessage(movieId, imageBytes, movie);
        rabbitTemplate.convertAndSend(RabbitConfig.VIDEO_QUEUE, message);
        log.info("Imagem com ID '{}' enviada para fila RabbitMQ para detecção facial", movieId);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "movies")
    public List<Movie> findAll() {

        movieRepository.findAll().forEach(movie -> log.info(movie.getClass().getName()));

        if (movieRepository.findAll().isEmpty()) {
            throw new IllegalArgumentException("No movies found");
        }
        return movieRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "movies", value = "movies", key = "#title")
    public List<Movie> findAllByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be null or blank");
        }
        List<Movie> movies = movieRepository.findAll().stream()
                .filter(movie -> movie.getTitle().equalsIgnoreCase(title))
                .toList();
        if (movies.isEmpty()) {
            throw new IllegalArgumentException("No movies found with title: " + title);
        }
        return movies;
    }

    public void storePendingMovie(UUID id, Movie movie) {
        tempMovieCache.put(id, movie);
    }

    public Movie getPendingMovie(UUID id) {
        return tempMovieCache.remove(id); // remove ao buscar
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "movies")
    public boolean existsById(UUID id) {
        return movieRepository.existsById(id);
    }

}
