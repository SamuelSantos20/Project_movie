package com.br.sb.project_movie.validation;

import com.br.sb.project_movie.model.Cast;
import com.br.sb.project_movie.repository.CastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CastValidation {

    private final CastRepository castRepository;


    public void validate(Cast cast) {

        if(IsValid(cast)) {
            throw new IllegalArgumentException("Movie is not valid");
        }
    }

    private boolean IsValid(Cast cast) {

        Optional<Cast> optionalMovie = castRepository.findByName(cast.getName());

        if (cast.getId() == null) {
            return optionalMovie.isPresent();
        }

        return optionalMovie.isPresent() && !optionalMovie.get().getId().equals(cast.getId());
    }

}
