package com.br.sb.project_movie.validation;

import com.br.sb.project_movie.model.Movie;
import com.br.sb.project_movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieValidation {
    private final MovieRepository movieRepository;

    public void validate(Movie movie) {

        if(IsValid(movie)) {
            throw new IllegalArgumentException("Movie is not valid");
        }
    }

    private boolean IsValid(Movie movie) {

        Optional<Movie> optionalMovie = movieRepository.findById(movie.getId());

        if (optionalMovie.isEmpty()) {
            return false;
        }

        return optionalMovie.stream().anyMatch( movie1 -> movie1.getTitle().equals(movie.getTitle()));
    }
}
