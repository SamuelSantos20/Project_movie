package com.br.sb.project_movie.service;

import com.br.sb.project_movie.model.Movie;
import com.br.sb.project_movie.repository.MovieRepository;
import com.br.sb.project_movie.validation.MovieValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class,readOnly = false)
public class MovieService {

    private final MovieRepository movieRepository;

    private final MovieValidation movieValidation;

    @CacheEvict(value = "movies", key = "#movie.getId()", allEntries = true)
    public Movie saveMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }
        movieValidation.validate(movie);
        if (movieRepository.existsByTitleAndIdNot(movie.getTitle(), movie.getId())) {
            throw new IllegalArgumentException("Movie with title '" + movie.getTitle() + "' already exists");
        }
        Movie save = movieRepository.save(movie);

        return save;
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "movies", key = "#id")
    public Movie findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Movie ID cannot be null");
        }
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));
    }
    public void deleteById(UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new IllegalArgumentException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }
    public void update(Movie movie) {
        if (movie == null || movie.getId() == null) {
            throw new IllegalArgumentException("Movie or Movie ID cannot be null");
        }
        movieValidation.validate(movie);
        if (!movieRepository.existsById(movie.getId())) {
            throw new IllegalArgumentException("Movie not found with id: " + movie.getId());
        }
        movieRepository.save(movie);
    }
    @Transactional(readOnly = true)
    public boolean existsByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be null or blank");
        }
        return movieRepository.existsByTitle(title);
    }
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "movies", value = "movies", key = "#title")
    public List<Movie> findAll() {
        if (movieRepository.findAll().isEmpty()) {
            throw new IllegalArgumentException("No movies found");
        }
        return movieRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable( cacheManager = "movies", value = "movies", key = "#title")
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

}
