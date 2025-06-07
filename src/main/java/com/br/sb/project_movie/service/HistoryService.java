package com.br.sb.project_movie.service;

import com.br.sb.project_movie.model.History;
import com.br.sb.project_movie.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
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
    public List<History> findAll() {
        List<History> histories = historyRepository.findAll();
        if (histories.isEmpty()) {
            throw new IllegalArgumentException("No histories found");
        }
        return histories;

    }

    @Transactional(readOnly = true)
    public History findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("History ID cannot be null");
        }
        return historyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("History not found with id: " + id));
    }

    public void update(History history) {
        if (history == null || history.getId() == null) {
            throw new IllegalArgumentException("History or History ID cannot be null");
        }
        if (!historyRepository.existsById(history.getId())) {
            throw new IllegalArgumentException("History not found with id: " + history.getId());
        }
        historyRepository.save(history);
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
