package com.monika.expense_tracker_backend.service;

import com.monika.expense_tracker_backend.model.User;

public interface UserService {
    User getUserByEmail(String email);
    User registerUser(User user);
}