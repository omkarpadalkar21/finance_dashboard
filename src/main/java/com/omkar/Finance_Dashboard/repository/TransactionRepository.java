package com.omkar.Finance_Dashboard.repository;

import com.omkar.Finance_Dashboard.model.Transaction;
import com.omkar.Finance_Dashboard.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByCategoryIgnoreCase(String category);
    List<Transaction> findByType(TransactionType type);

    @Query("SELECT SUM(t.amount) FROM transactions t WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") TransactionType type);

    @Query("SELECT t.category, SUM(t.amount) FROM transactions t WHERE t.type = :type GROUP BY t.category")
    List<Object[]> sumAmountByCategoryAndType(@Param("type") TransactionType type);
    
    List<Transaction> findTop10ByOrderByDateDesc();
}
