package com.monika.expensetrackerbackend.controller;

import com.monika.expensetrackerbackend.dto.ExpenseRequest;
import com.monika.expensetrackerbackend.dto.ExpenseResponse;
import com.monika.expensetrackerbackend.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/user/{userId}")
    public ExpenseResponse addExpense(@PathVariable Long userId, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.addExpense(request, userId);
    }

    @GetMapping("/user/{userId}")
    public List<ExpenseResponse> getExpenses(@PathVariable Long userId) {
        return expenseService.getExpensesByUser(userId);
    }

    @PutMapping("/{expenseId}")
    public ExpenseResponse updateExpense(@PathVariable Long expenseId, @RequestBody ExpenseRequest request) {
        return expenseService.updateExpense(expenseId, request);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
    }
}