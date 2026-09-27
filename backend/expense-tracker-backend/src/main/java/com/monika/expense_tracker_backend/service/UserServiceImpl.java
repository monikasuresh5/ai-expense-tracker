package com.monika.expense_tracker_backend.service;

import com.monika.expense_tracker_backend.exception.ResourceNotFoundException;
import com.monika.expense_tracker_backend.model.User;
import com.monika.expense_tracker_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }
}