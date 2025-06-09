package com.br.sb.project_movie.model;

import com.br.sb.project_movie.controller.HistoryController;
import com.br.sb.project_movie.dto.HistoryDto;
import com.br.sb.project_movie.mapper.HistoryMapper;
import com.br.sb.project_movie.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions .*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito .*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HistoryTest {

    @InjectMocks
    private HistoryController historyController;

    @Mock
    private HistoryMapper historyMapper;

    @Mock
    private HistoryService historyService;

    private History history;
    private HistoryDto historyDto;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Movie movie = new Movie();
        movie.setId(UUID.randomUUID());

        history = new History();
        history.setId(UUID.randomUUID());
        history.setUser(user);
        history.setMovie(movie);
        history.setViewDate(LocalDateTime.now());
        history.setRating(5);

        historyDto = new HistoryDto(
                history.getId(),
                history.getUser(),
                history.getMovie(),
                history.getRating(),
                history.getViewDate()
        );
    }

    @Test
    void testHistoryCreation() {
        assertNotNull(historyDto);
        assertEquals(history.getId(), historyDto.id());
        assertEquals(history.getUser(), historyDto.user());
        assertEquals(history.getMovie(), historyDto.movie());
        assertEquals(history.getRating(), historyDto.rating());
        assertEquals(history.getViewDate(), historyDto.viewDate());
    }

    @Test
    void testHistorySave() {
        when(historyMapper.toModel(any(HistoryDto.class))).thenReturn(history);
        when(historyService.saveHistory(any(History.class))).thenReturn(history);
        when(historyMapper.toDto(any(History.class))).thenReturn(historyDto);

        ResponseEntity<Object> response = historyController.createHistory(historyDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(historyService, times(1)).saveHistory(any(History.class));
    }

    @Test
    void testHistoryUpdate() {
        when(historyMapper.toModel(any(HistoryDto.class))).thenReturn(history);
        when(historyService.update(any(History.class))).thenReturn(history);
        when(historyMapper.toDto(any(History.class))).thenReturn(historyDto);

        ResponseEntity<Object> response = historyController.updateHistory(historyDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(historyService, times(1)).update(any(History.class));
    }

    @Test
    void testHistoryFindById() {
        when(historyService.findById(any(UUID.class))).thenReturn(history);
        when(historyMapper.toDto(any(History.class))).thenReturn(historyDto);

        ResponseEntity<Object> response = historyController.findHistoryById(history.getId().toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(historyService, times(1)).findById(any(UUID.class));
    }

    @Test
    void testHistoryFindAll() {
        when(historyService.findAll()).thenReturn(List.of(history));
        when(historyMapper.toDto(any(History.class))).thenReturn(historyDto);

        ResponseEntity<Object> response = historyController.findAllHistory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(historyService, times(1)).findAll();
    }

    @Test
    void testHistoryDeletion() {
        doNothing().when(historyService).deleteById(history.getId());

        ResponseEntity<Object> response = historyController.deleteHistory(history.getId().toString());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(historyService, times(1)).deleteById(history.getId());
    }
}
