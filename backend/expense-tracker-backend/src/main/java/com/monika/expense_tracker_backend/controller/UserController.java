package com.monika.expense_tracker_backend.controller;

import com.monika.expense_tracker_backend.exception.ResourceNotFoundException;
import com.monika.expense_tracker_backend.model.User;
import com.monika.expense_tracker_backend.service.UserService;
import com.monika.expense_tracker_backend.service.UserServiceImpl;
import com.monika.expense_tracker_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserServiceImpl userServiceImpl;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User loginRequest) {
        User user;
        try {
            user = userService.getUserByEmail(loginRequest.getEmail());
        } catch (ResourceNotFoundException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!userServiceImpl.checkPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return Map.of("token", jwtUtil.generateToken(user.getEmail()), "email", user.getEmail());
    }
}