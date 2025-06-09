package com.br.sb.project_movie.validation;

import com.br.sb.project_movie.dto.UserDto;
import com.br.sb.project_movie.model.User;
import com.br.sb.project_movie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;

    public void validate(UserDto userDto) {
        if (isInvalid(userDto)) {
            throw new IllegalArgumentException("User is not valid");
        }
    }

    private boolean isInvalid(UserDto userDto) {
        Optional<User> userWithEmail = userRepository.findByEmail(userDto.email());

        if (userDto.id() == null) {
            // Criação: inválido se email já existe
            return userWithEmail.isPresent();
        }

        // Atualização: inválido se email pertence a outro usuário
        return userWithEmail.isPresent() && !userWithEmail.get().getId().equals(userDto.id());
    }

}
