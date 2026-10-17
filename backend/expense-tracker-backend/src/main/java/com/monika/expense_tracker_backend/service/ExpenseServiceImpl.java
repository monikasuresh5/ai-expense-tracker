package com.monika.expense_tracker_backend.service;

import com.monika.expense_tracker_backend.dto.ExpenseRequest;
import com.monika.expense_tracker_backend.dto.ExpenseResponse;
import com.monika.expense_tracker_backend.exception.ResourceNotFoundException;
import com.monika.expense_tracker_backend.model.Expense;
import com.monika.expense_tracker_backend.model.User;
import com.monika.expense_tracker_backend.repository.ExpenseRepository;
import com.monika.expense_tracker_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Override
    public ExpenseResponse addExpense(ExpenseRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Expense expense = Expense.builder()
                .title(request.getTitle())
                .amount(request.getAmount())
                .category(request.getCategory())
                .date(request.getDate())
                .description(request.getDescription())
                .user(user)
                .build();
        return mapToResponse(expenseRepository.save(expense));
    }

    @Override
    public List<ExpenseResponse> getExpensesByUserEmail(String email) {
        return expenseRepository.findByUserEmailOrderByDateDesc(email)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request, String email) {
        Expense expense = expenseRepository.findByIdAndUserEmail(expenseId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        return mapToResponse(expenseRepository.save(expense));
    }

    @Override
    public void deleteExpense(Long expenseId, String email) {
        Expense expense = expenseRepository.findByIdAndUserEmail(expenseId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        expenseRepository.delete(expense);
    }

    private ExpenseResponse mapToResponse(Expense e) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .amount(e.getAmount())
                .category(e.getCategory())
                .date(e.getDate())
                .description(e.getDescription())
                .build();
    }
}