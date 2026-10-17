package com.monika.expense_tracker_backend.repository;

import com.monika.expense_tracker_backend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserEmailOrderByDateDesc(String email);
    Optional<Expense> findByIdAndUserEmail(Long id, String email);
}