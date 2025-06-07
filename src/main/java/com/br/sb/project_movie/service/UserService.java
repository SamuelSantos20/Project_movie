package com.br.sb.project_movie.service;

import com.br.sb.project_movie.model.User;
import com.br.sb.project_movie.repository.UserRepository;
import com.br.sb.project_movie.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class,readOnly = false)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserValidation userValidation;


    public User save(User user) {
        userValidation.validate(user);
        User savedUser = userRepository.save(user);
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

    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)){
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }


    public User update(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User or User ID cannot be null");
        }
        userValidation.validate(user);
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }
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


    public boolean existsByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("User title cannot be null or blank");
        }
        return userRepository.existsByTitle(title);
    }
}
