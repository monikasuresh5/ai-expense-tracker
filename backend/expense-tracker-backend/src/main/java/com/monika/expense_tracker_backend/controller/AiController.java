package com.monika.expense_tracker_backend.controller;

import com.monika.expense_tracker_backend.dto.ExpenseResponse;
import com.monika.expense_tracker_backend.model.Category;
import com.monika.expense_tracker_backend.service.ExpenseService;
import com.monika.expense_tracker_backend.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiService geminiService;
    private final ExpenseService expenseService;

    @PostMapping("/categorize")
    public Map<String, String> categorize(@RequestBody Map<String, String> body) {
        String prompt = "Classify this expense into exactly one of: FOOD, TRAVEL, BILLS, SHOPPING, "
                + "ENTERTAINMENT, HEALTH, EDUCATION, OTHER. Reply with only the category word.\n"
                + "Title: " + body.getOrDefault("title", "") + "\n"
                + "Description: " + body.getOrDefault("description", "");

        String answer = geminiService.ask(prompt).trim().toUpperCase();
        String category = Category.isValid(answer) ? answer : "OTHER";
        return Map.of("category", category);
    }

    @GetMapping("/summary")
    public Map<String, String> summary(Authentication auth) {
        YearMonth now = YearMonth.now();
        List<ExpenseResponse> thisMonth = expenseService.getExpensesByUserEmail(auth.getName())
                .stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(now))
                .toList();

        if (thisMonth.isEmpty()) {
            return Map.of("summary", "No expenses recorded this month yet.");
        }

        Map<String, Double> totals = new TreeMap<>();
        double sum = 0;
        for (ExpenseResponse e : thisMonth) {
            String cat = e.getCategory() == null ? "OTHER" : e.getCategory();
            totals.merge(cat, e.getAmount(), Double::sum);
            sum += e.getAmount();
        }

        StringBuilder data = new StringBuilder();
        totals.forEach((k, v) -> data.append(k).append(": ₹").append(String.format("%.2f", v)).append("\n"));

        String prompt = "You are a friendly personal finance assistant. Amounts are in Indian rupees (₹). "
                + "Here is this month's spending by category (total ₹" + String.format("%.2f", sum) + "):\n"
                + data
                + "Write a short summary (4-5 sentences): where most money went, anything that looks high, "
                + "and 2 practical saving tips. Plain text, no markdown.";

        return Map.of("summary", geminiService.ask(prompt));
    }
}