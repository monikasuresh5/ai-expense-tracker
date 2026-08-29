package com.monika.expensetrackerbackend.service;

import com.monika.expensetrackerbackend.model.User;

public interface UserService {
    User getUserByEmail(String email);
    User registerUser(User user);
}