package com.omkar.Finance_Dashboard.service;

import com.omkar.Finance_Dashboard.dto.TransactionRequest;
import com.omkar.Finance_Dashboard.dto.TransactionResponse;
import com.omkar.Finance_Dashboard.model.Transaction;
import com.omkar.Finance_Dashboard.model.User;
import com.omkar.Finance_Dashboard.repository.TransactionRepository;
import com.omkar.Finance_Dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResponse createTransaction(TransactionRequest request) {
        User currentUser = getCurrentUser();
        
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .user(currentUser)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    public TransactionResponse getTransaction(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapToResponse(transaction);
    }

    public List<TransactionResponse> getAllTransactions(String startDate, String endDate, String category, String type) {
        List<Transaction> transactions = transactionRepository.findAll();

        if (startDate != null) {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate);
            transactions = transactions.stream().filter(t -> !t.getDate().isBefore(start)).collect(Collectors.toList());
        }
        if (endDate != null) {
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate);
            transactions = transactions.stream().filter(t -> !t.getDate().isAfter(end)).collect(Collectors.toList());
        }
        if (category != null) {
            transactions = transactions.stream().filter(t -> t.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
        }
        if (type != null) {
            com.omkar.Finance_Dashboard.model.TransactionType transactionType = com.omkar.Finance_Dashboard.model.TransactionType.valueOf(type.toUpperCase());
            transactions = transactions.stream().filter(t -> t.getType() == transactionType).collect(Collectors.toList());
        }

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse updateTransaction(UUID id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setNotes(request.getNotes());

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    public void deleteTransaction(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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
