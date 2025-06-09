package com.br.sb.project_movie.service;

import com.br.sb.project_movie.dto.UserDto;
import com.br.sb.project_movie.mapper.UserMapper;
import com.br.sb.project_movie.model.User;
import com.br.sb.project_movie.repository.UserRepository;
import com.br.sb.project_movie.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class,readOnly = false)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserValidation userValidation;

    private final UserMapper userMapper;

    public User save(UserDto userDto) {
        userValidation.validate(userDto);
        User model = userMapper.toModel(userDto);
        User savedUser = userRepository.save(model);
        return savedUser;
    }
    @Transactional(readOnly = true)
    public User findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("User email cannot be null or blank");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)){
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }


    public User update(UserDto userDto) {
        if (userDto == null || userDto.id() == null) {
            throw new IllegalArgumentException("User or User ID cannot be null");
        }
        userValidation.validate(userDto);
        if (!userRepository.existsById(userDto.id())) {
            throw new IllegalArgumentException("User not found with id: " + userDto.id());
        }


        Optional<User> optionalUser = userRepository.findById(userDto.id());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + userDto.id());
        }
        User user = userMapper.partialUpdate(userDto, optionalUser.get());
        User save = userRepository.save(user);
        return save;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new IllegalArgumentException("No users found");
        }
        return users;

    }
}
