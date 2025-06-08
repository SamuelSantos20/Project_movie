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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;

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
    }

    @Test
    void testUserCreation() {
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getRole(), user.getCreatedAt(), user.getUpdatedAt());

        when(userMapper.toModel(any(UserDto.class))).thenReturn(user);
        when(userService.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto); // <-- mock necessário

        ResponseEntity<Object> response = userController.createUser(userDto);

        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody());
    }

    @Test
    void testUserUpdate() {
        when(userService.update(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getRole(), user.getCreatedAt(), user.getUpdatedAt()));
        when(userMapper.toModel(any(UserDto.class))).thenReturn(user);
        ResponseEntity<Object> response = userController.updateUser(userMapper.toDto(user));
        assertNotNull(response.getBody());
        assertEquals(user.getId(), ((UserDto) response.getBody()).id());
    }
    @Test
    void testUserDeletion() {
        doNothing().when(userService).deleteById((user.getId()));
        ResponseEntity<Object> response = userController.deleteUser(String.valueOf(user.getId()));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteById(user.getId());
    }
}