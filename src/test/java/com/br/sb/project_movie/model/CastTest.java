package com.br.sb.project_movie.model;

import com.br.sb.project_movie.controller.CastController;
import com.br.sb.project_movie.dto.CastDto;
import com.br.sb.project_movie.mapper.CastMapper;
import com.br.sb.project_movie.service.CastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CastTest {

    @Mock
    private Cast cast;

    @InjectMocks
    private CastController castController;

    @Mock
    private CastService castService;

    @Mock
    private CastMapper castMapper;

    @Mock
    private CastDto castDto;



    @BeforeEach
    void setUp() {

        cast = new Cast();
        cast.setId(UUID.randomUUID());
        cast.setName("Test Actor");
        cast.setBirthDate(LocalDate.now());
        cast.setNationality("Test Nationality");

        castDto = new CastDto(cast.getId(), cast.getName(), cast.getBirthDate(), cast.getNationality());
    }

    @Test
    void testCastCreation() {
        when(castService.saveCast(cast)).thenReturn(cast);
        when(castMapper.toModel(castDto)).thenReturn(cast);
        when(castMapper.toDto(cast)).thenReturn(castDto);

        ResponseEntity<Object> response = castController.saveCast(castDto);


        assertNotNull(response.getBody());
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(castDto, response.getBody());

        verify(castService).saveCast(cast);
    }

    @Test
    void testCastUpdate() {
        when(castService.update(cast)).thenReturn(cast);
        when(castMapper.toModel(castDto)).thenReturn(cast);
        when(castMapper.toDto(cast)).thenReturn(castDto);

        ResponseEntity<?> response = castController.updateCast(castDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(castDto, response.getBody());

        verify(castService).update(cast);
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        when(castService.findById(id)).thenReturn(java.util.Optional.of(cast));
        when(castMapper.toDto(cast)).thenReturn(castDto);

        ResponseEntity<Object> response = castController.findById(id);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(castDto, response.getBody());

        verify(castService).findById(id);
    }

    @Test
    void testFindAllCast() {
        when(castService.findAllCast()).thenReturn(List.of(cast));
        when(castMapper.toDto(cast)).thenReturn(castDto);

        ResponseEntity<?> response = castController.findAllCast();

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(castService).findAllCast();
    }

    @Test
    void testDeleteById() {
        UUID id = UUID.randomUUID();
        doNothing().when(castService).deleteById(id);
        ResponseEntity<Object> response = castController.deleteCast(UUID.fromString(id.toString()));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(castService).deleteById(id);
    }
}