package com.monika.expensetrackerbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/api/health")
    public String healthCheck() {
        return "AI Expense Tracker backend is running!";
    }
}