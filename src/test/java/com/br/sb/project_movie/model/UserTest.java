package com.br.sb.project_movie.model;

import com.br.sb.project_movie.controller.UserController;
import com.br.sb.project_movie.dto.UserDto;
import com.br.sb.project_movie.mapper.UserMapper;
import com.br.sb.project_movie.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setRole("USER");
        user.setEmail("test@test.com");
        user.setPassword("password123");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userDto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getRole(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Test
    void testUserCreation() {
        when(userService.save(userDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto); // Stub toDto to avoid null

        ResponseEntity<Object> response = userController.createUser(userDto);

        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody());

        verify(userService).save(userDto);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userService, userMapper);
    }

    @Test
    void testUserUpdate() {
        when(userService.update(userDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseEntity<Object> response = userController.updateUser(userDto);

        assertNotNull(response.getBody());
        assertEquals(userDto, response.getBody());

        verify(userService).update(userDto);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userService, userMapper);
    }

    @Test
    void testUserDeletion() {
        doNothing().when(userService).deleteById(user.getId());
        ResponseEntity<Object> response = userController.deleteUser(String.valueOf(user.getId()));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteById(user.getId());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(userMapper);
    }
}