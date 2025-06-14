package com.br.sb.project_movie.service;

import com.br.sb.project_movie.model.Cast;
import com.br.sb.project_movie.repository.CastRepository;
import com.br.sb.project_movie.validation.CastValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class CastService {

    private final CastRepository castRepository;
    private final CastValidation castValidation;


    @CacheEvict(value = "casts", allEntries = true)
    public Cast saveCast(Cast cast) {
        if (cast == null) {
            throw new IllegalArgumentException("Cast cannot be null");
        }
        castValidation.validate(cast);
        return castRepository.save(cast);

    }

    @Transactional(readOnly = true)
    @Cacheable(value = "casts", key = "#id")
    public Optional<Cast> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Cast ID cannot be null");
        }

        return Optional.ofNullable(castRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cast not found with id: " + id)));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "casts")
    public List<Cast> findAllCast() {
        List<Cast> casts = castRepository.findAll();
        if (!casts.iterator().hasNext()) {
            throw new IllegalArgumentException("No casts found");
        }
        return casts;
    }

    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Cast ID cannot be null");
        }
        if (!castRepository.existsById(id)) {
            throw new IllegalArgumentException("Cast not found with id: " + id);
        }
        castRepository.deleteById(id);
    }

    @CacheEvict(value = "casts")
    public Cast update(Cast cast) {
        if (cast == null || cast.getId() == null) {
            throw new IllegalArgumentException("Cast or Cast ID cannot be null");
        }
        if (!castRepository.existsById(cast.getId())) {
            throw new IllegalArgumentException("Cast not found with id: " + cast.getId());
        }
        castValidation.validate(cast);
        return castRepository.save(cast);
    }
}
