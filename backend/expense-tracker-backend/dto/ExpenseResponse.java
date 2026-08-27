package com.monika.expensetrackerbackend.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpenseResponse {
    private Long id;
    private String title;
    private Double amount;
    private String category;
    private LocalDate date;
    private String description;
}