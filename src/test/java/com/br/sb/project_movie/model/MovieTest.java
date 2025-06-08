package com.br.sb.project_movie.model;

import com.br.sb.project_movie.controller.MovieController;
import com.br.sb.project_movie.dto.MovieDto;
import com.br.sb.project_movie.mapper.MovieMapper;
import com.br.sb.project_movie.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MovieTest {
    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @Mock
    private MovieMapper movieMapper;

    private Movie movie;

     @BeforeEach
    void setUp() {
         movie = new Movie();
         movie.setId(UUID.randomUUID());
         movie.setTitle("Test Movie");
         movie.setDescription("This is a test movie description.");
         movie.setReleaseDate(LocalDateTime.now());
         movie.setCreatedAt(LocalDateTime.now());
         movie.setUpdatedAt(LocalDateTime.now());
     }

    @Test
    void testMovieCreation() {
        MovieDto movieDto = new MovieDto(movie.getId(), movie.getTitle(), movie.getDirector(),movie.getGenre(), movie.getReleaseDate(), movie.getDescription(), movie.getRating(), movie.getImage(), movie.getTrailer(), movie.getCreatedAt(), movie.getUpdatedAt());

        when(movieMapper.toModel(any(MovieDto.class))).thenReturn(movie);
        when(movieService.saveMovie(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(movie)).thenReturn(movieDto); // <-- mock necessário

        ResponseEntity<Object> response = movieController.createMovie(movieDto);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    @Test
    void testUpdateMovie() {
        MovieDto movieDto = new MovieDto(movie.getId(), movie.getTitle(), movie.getDirector(),movie.getGenre(), movie.getReleaseDate(), movie.getDescription(), movie.getRating(), movie.getImage(), movie.getTrailer(), movie.getCreatedAt(), movie.getUpdatedAt());

        when(movieMapper.toModel(any(MovieDto.class))).thenReturn(movie);
        when(movieService.update(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(movie)).thenReturn(movieDto);

        ResponseEntity<Object> response = movieController.updateMovie(movieDto);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testFindMovieById() {
        UUID movieId = UUID.randomUUID();
        movie.setId(movieId);
        MovieDto movieDto = new MovieDto(movie.getId(), movie.getTitle(), movie.getDirector(),movie.getGenre(), movie.getReleaseDate(), movie.getDescription(), movie.getRating(), movie.getImage(), movie.getTrailer(), movie.getCreatedAt(), movie.getUpdatedAt());

        when(movieService.findById(movieId)).thenReturn(movie);
        when(movieMapper.toDto(movie)).thenReturn(movieDto);

        ResponseEntity<Object> response = movieController.findMovieById(movieId.toString());

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void testFindAllMovies() {
        when(movieService.findAll()).thenReturn(List.of());
        ResponseEntity<Object> response = movieController.findAllMovies();
        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
    @Test
    void testDeleteMovie() {
        doNothing().when(movieService).deleteById((movie.getId()));
        ResponseEntity<Object> response = movieController.deleteMovie(String.valueOf(movie.getId()));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).deleteById(movie.getId());

    }

}