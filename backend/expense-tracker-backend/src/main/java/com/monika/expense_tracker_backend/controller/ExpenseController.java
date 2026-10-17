package com.monika.expense_tracker_backend.controller;

import com.monika.expense_tracker_backend.dto.ExpenseRequest;
import com.monika.expense_tracker_backend.dto.ExpenseResponse;
import com.monika.expense_tracker_backend.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ExpenseResponse add(@Valid @RequestBody ExpenseRequest request, Authentication auth) {
        return expenseService.addExpense(request, auth.getName());
    }

    @GetMapping
    public List<ExpenseResponse> list(Authentication auth) {
        return expenseService.getExpensesByUserEmail(auth.getName());
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request, Authentication auth) {
        return expenseService.updateExpense(id, request, auth.getName());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        expenseService.deleteExpense(id, auth.getName());
    }
}