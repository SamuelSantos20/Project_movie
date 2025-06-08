package com.br.sb.project_movie.controller;

import com.br.sb.project_movie.dto.UserDto;
import com.br.sb.project_movie.mapper.UserMapper;
import com.br.sb.project_movie.model.User;
import com.br.sb.project_movie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements GenericController {

    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(UserDto userDto) {
        User user = userMapper.toModel(userDto);
        User savedUser = userService.save(user);
        HttpHeaders headers = gerarHaderLoccation("/users/" + savedUser.getId());
        return new ResponseEntity<>(savedUser.getId(), headers, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid UserDto userDto) {

        if (userDto == null || userDto.id() == null) {
            return ResponseEntity.badRequest().build();
        }

        User user = userMapper.toModel(userDto);

        UserDto dto = userMapper.toDto(userService.update(user));

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteUser(@RequestParam(value = "id", required = false) UUID id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") String id) {
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        id = id.trim();
        UUID uuid = UUID.fromString(id);

        User user = userService.findById(uuid);

        UserDto userDto = userMapper.toDto(user);

        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/find/all")
    public ResponseEntity<Object> findAll() {
        var users = userService.findAll();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var userDtos = users.stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(userDtos);
    }

}
