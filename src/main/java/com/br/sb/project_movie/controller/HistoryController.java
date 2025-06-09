package com.br.sb.project_movie.controller;

import com.br.sb.project_movie.dto.HistoryDto;
import com.br.sb.project_movie.mapper.HistoryMapper;
import com.br.sb.project_movie.model.History;
import com.br.sb.project_movie.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/history")
public class HistoryController implements GenericController {

    private final HistoryService historyService;

    private final HistoryMapper historyMapper;

    @PostMapping("/create")
    public ResponseEntity<Object> createHistory( @RequestBody @Valid HistoryDto historyDto) {
        History history = historyMapper.toModel(historyDto);
        HistoryDto savedHistory = historyMapper.toDto(historyService.saveHistory(history));
        HttpHeaders headers = gerarHaderLoccation("/history/" + savedHistory.id());
        return new ResponseEntity<>(savedHistory.id(), headers, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateHistory( @RequestBody @Valid HistoryDto historyDto) {
        History history = historyMapper.toModel(historyDto);
        HistoryDto savedHistory = historyMapper.toDto(historyService.update(history));
        HttpHeaders headers = gerarHaderLoccation("/history/" + savedHistory.id());
        return new ResponseEntity<>(savedHistory.id(), headers, HttpStatus.CREATED);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Object> findHistoryById(@PathVariable("id") String id) {
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        id = id.trim();
        UUID uuid = UUID.fromString(id);
        History history = historyService.findById(uuid);
        HistoryDto historyDto = historyMapper.toDto(history);
        return ResponseEntity.ok(historyDto);
    }

    @GetMapping("/find")
    public ResponseEntity<Object> findAllHistory() {
        var histories = historyService.findAll();
        if (histories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var historyDtos = histories.stream()
                .map(historyMapper::toDto)
                .toList();
        return ResponseEntity.ok(historyDtos);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteHistory(@RequestParam(value = "id", required = false) String id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        UUID uuid = UUID.fromString(id);
        historyService.deleteById(uuid);
        return ResponseEntity.noContent().build();

    }
}
