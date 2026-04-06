package com.omkar.Finance_Dashboard.dto;

import com.omkar.Finance_Dashboard.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {
    private UUID id;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDateTime date;
    private String notes;
    private UUID userId;
}
