package com.br.sb.project_movie.model;

import com.br.sb.project_movie.controller.UserController;
import com.br.sb.project_movie.dto.UserDto;
import com.br.sb.project_movie.mapper.UserMapper;
import com.br.sb.project_movie.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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
        when(userService.save(any(User.class))).thenReturn(user);
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getRole(), user.getCreatedAt(), user.getUpdatedAt());
        when(userMapper.toModel(any(UserDto.class))).thenReturn(user);
        ResponseEntity<Object> response = userController.createUser(userDto);
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody());

    }

    @Test
    void testUserUpdate() {
        when(userService.update(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getRole(), user.getCreatedAt(), user.getUpdatedAt()));
        ResponseEntity<Object> response  = userController.updateUser(userMapper.toDto(user));
        assertNotNull(response.getBody());
        assertEquals(user.getId(), ((UserDto) response.getBody()).id());
    }



    @Test
    void testUserDeletion() {
        ResponseEntity<Object> response = userController.deleteUser(user.getId());

    }
}