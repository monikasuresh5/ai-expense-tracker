package com.monika.expense_tracker_backend.service;

import com.monika.expense_tracker_backend.dto.ExpenseRequest;
import com.monika.expense_tracker_backend.dto.ExpenseResponse;

import java.util.List;

public interface ExpenseService {
    ExpenseResponse addExpense(ExpenseRequest request, String email);
    List<ExpenseResponse> getExpensesByUserEmail(String email);
    ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request, String email);
    void deleteExpense(Long expenseId, String email);
}