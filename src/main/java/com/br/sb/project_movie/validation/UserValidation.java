package com.br.sb.project_movie.validation;

import com.br.sb.project_movie.model.User;
import com.br.sb.project_movie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;

    public void validate(User user) {

        if (isValid(user)) {
            throw new IllegalArgumentException("User is not valid");
        }
    }

    private boolean isValid(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getId());
        ;

        if (optionalUser.isEmpty()) {
            return false;
        }


        return optionalUser.stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()));
    }

}
