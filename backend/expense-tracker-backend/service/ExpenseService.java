package com.monika.expensetrackerbackend.service;

import com.monika.expensetrackerbackend.dto.ExpenseRequest;
import com.monika.expensetrackerbackend.dto.ExpenseResponse;

import java.util.List;

public interface ExpenseService {
    ExpenseResponse addExpense(ExpenseRequest request, Long userId);
    List<ExpenseResponse> getExpensesByUser(Long userId);
    ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request);
    void deleteExpense(Long expenseId);
}