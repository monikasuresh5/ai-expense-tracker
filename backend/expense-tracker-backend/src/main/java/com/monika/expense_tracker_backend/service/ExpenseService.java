package com.monika.expense_tracker_backend.service;

import com.monika.expense_tracker_backend.dto.ExpenseRequest;
import com.monika.expense_tracker_backend.dto.ExpenseResponse;

import java.util.List;

public interface ExpenseService {
    ExpenseResponse addExpense(ExpenseRequest request, Long userId);
    List<ExpenseResponse> getExpensesByUser(Long userId);
    ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request);
    void deleteExpense(Long expenseId);
}