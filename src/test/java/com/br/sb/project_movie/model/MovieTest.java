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
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieTest {
    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @Mock
    private MovieMapper movieMapper;

    private Movie movie;
    private List<Cast> elenco;
    private MovieDto movieDto;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(UUID.randomUUID());
        movie.setTitle("Test Movie");
        movie.setDirector("Test Director");
        movie.setGenre(Genero.ACAO);
        movie.setReleaseDate(LocalDate.of(2023, 1, 1));
        movie.setDescription("Test Description");
        movie.setRating(8.0);
        movie.setDuration(Duration.ofMinutes(120));
        movie.setImage("imageBytes".getBytes());
        movie.setTrailer("trailerBytes".getBytes());
        movie.setCreatedAt(LocalDateTime.now());
        movie.setUpdatedAt(LocalDateTime.now());
        movie.setClassification("PG-13");

        elenco = new ArrayList<>();
        elenco.add(new Cast(UUID.randomUUID(), "Actor 1", LocalDate.now(), "Brazilian"));
        movie.setCast(elenco);

        movieDto = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getGenre(),
                movie.getReleaseDate(),
                movie.getDescription(),
                movie.getRating(),
                elenco,
                movie.getDuration(),
                movie.getClassification(),
                movie.getImage(),
                movie.getTrailer(),
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }

    @Test
    void testMovieCreation() throws IOException {
        when(movieMapper.toModel(any(MovieDto.class))).thenReturn(movie);
        when(movieService.saveMovie(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(any(Movie.class))).thenReturn(movieDto);

        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "imageBytes".getBytes());
        MockMultipartFile trailer = new MockMultipartFile("trailer", "trailer.mp4", "video/mp4", "trailerBytes".getBytes());

        ResponseEntity<?> response = movieController.createMovie(movieDto, image, trailer);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof MovieDto);
        assertEquals(movieDto, response.getBody());
        verify(movieService, times(1)).saveMovie(any(Movie.class));
    }

    @Test
    void testUpdateMovie() throws IOException {
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "imageBytes".getBytes());
        MockMultipartFile trailer = new MockMultipartFile("trailer", "trailer.mp4", "video/mp4", "trailerBytes".getBytes());

        when(movieService.findById(movieDto.id())).thenReturn(movie);
        when(movieService.update(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(any(Movie.class))).thenReturn(movieDto);

        ResponseEntity<?> response = movieController.updateMovie(movieDto, image, trailer);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(movieService, times(1)).findById(movieDto.id());
        verify(movieService, times(1)).update(any(Movie.class));
    }

    @Test
    void testFindMovieById() {
        UUID movieId = movie.getId();
        when(movieService.findById(movieId)).thenReturn(movie);
        when(movieMapper.toDto(any( Movie.class))).thenReturn(movieDto);

        ResponseEntity<Object> response = movieController.findMovieById(movieId.toString());

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MovieDto);
        assertEquals(movieDto, response.getBody());
        verify(movieService, times(1)).findById(movieId);
    }

    @Test
    void testFindAllMovies() {
        List<MovieDto> movieDtos = List.of(movieDto);
        when(movieService.findAll()).thenReturn(List.of(movie));
        when(movieMapper.toDto(any(Movie.class))).thenReturn(movieDto);

        ResponseEntity<?> response = movieController.findAllMovies();

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movieDtos, response.getBody());
        verify(movieService, times(1)).findAll();
    }

    @Test
    void testDeleteMovie() {
        UUID movieId = movie.getId();
        doNothing().when(movieService).deleteById(movieId);

        ResponseEntity<Object> response = movieController.deleteMovie(movieId.toString());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(movieService, times(1)).deleteById(movieId);
    }
}