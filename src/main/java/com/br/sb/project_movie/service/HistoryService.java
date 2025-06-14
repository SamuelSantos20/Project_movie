package com.br.sb.project_movie.service;

import com.br.sb.project_movie.model.History;
import com.br.sb.project_movie.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(
        rollbackFor = Exception.class,
        readOnly = false)
public class HistoryService {

    private final HistoryRepository historyRepository;

    @CacheEvict(value = "histories", allEntries = true)
    public History saveHistory(History history) {
        if (history == null) {
            throw new IllegalArgumentException("History cannot be null");
        }
        if (history.getUser() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return historyRepository.save(history);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "histories")
    public List<History> findAll() {
        List<History> histories = historyRepository.findByAllHistory();
        if (histories.isEmpty()) {
            throw new IllegalArgumentException("No histories found");
        }
        return histories;

    }

    @Transactional(readOnly = true)
    @Cacheable(value = "histories")
    public History findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("History ID cannot be null");
        }

        History history = historyRepository.findByIdWithAll(id)
                .orElseThrow(() -> new IllegalArgumentException("History not found with id: " + id));

        return history;

    }

    @CacheEvict(value = "histories", allEntries = true)
    public History update(History history) {
        if (history == null || history.getId() == null) {
            throw new IllegalArgumentException("History or History ID cannot be null");
        }
        if (!historyRepository.existsById(history.getId())) {
            throw new IllegalArgumentException("History not found with id: " + history.getId());
        }
        return historyRepository.save(history);
    }

    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("History ID cannot be null");
        }
        if (!historyRepository.existsById(id)) {
            throw new IllegalArgumentException("History not found with id: " + id);
        }
        historyRepository.deleteById(id);
    }
}
