package com.omkar.Finance_Dashboard.service;

import com.omkar.Finance_Dashboard.dto.DashboardSummaryResponse;
import com.omkar.Finance_Dashboard.dto.TransactionResponse;
import com.omkar.Finance_Dashboard.model.Transaction;
import com.omkar.Finance_Dashboard.model.TransactionType;
import com.omkar.Finance_Dashboard.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardSummaryResponse getSummary() {
        BigDecimal totalIncome = transactionRepository.sumAmountByType(TransactionType.INCOME);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        BigDecimal totalExpenses = transactionRepository.sumAmountByType(TransactionType.EXPENSE);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        List<Object[]> incomeCategories = transactionRepository.sumAmountByCategoryAndType(TransactionType.INCOME);
        Map<String, BigDecimal> incomeMap = new HashMap<>();
        for (Object[] obj : incomeCategories) {
            incomeMap.put((String) obj[0], (BigDecimal) obj[1]);
        }

        List<Object[]> expenseCategories = transactionRepository.sumAmountByCategoryAndType(TransactionType.EXPENSE);
        Map<String, BigDecimal> expenseMap = new HashMap<>();
        for (Object[] obj : expenseCategories) {
            expenseMap.put((String) obj[0], (BigDecimal) obj[1]);
        }

        List<TransactionResponse> recentActivity = transactionRepository.findTop10ByOrderByDateDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryWiseTotalsIncome(incomeMap)
                .categoryWiseTotalsExpense(expenseMap)
                .recentActivity(recentActivity)
                .build();
    }

    private TransactionResponse mapToResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .type(t.getType())
                .category(t.getCategory())
                .date(t.getDate())
                .notes(t.getNotes())
                .userId(t.getUser().getId())
                .build();
    }
}
