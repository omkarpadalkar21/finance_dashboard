package com.omkar.Finance_Dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryWiseTotalsIncome;
    private Map<String, BigDecimal> categoryWiseTotalsExpense;
    private List<TransactionResponse> recentActivity;
}
