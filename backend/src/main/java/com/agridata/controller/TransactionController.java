package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import com.agridata.model.Transaction;
import com.agridata.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/buy/{assetId}")
    public ResponseEntity<ApiResponse<Transaction>> buyAsset(@PathVariable String assetId, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            Transaction transaction = transactionService.createPurchaseTransaction(assetId, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("购买成功", transaction));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("购买失败: " + e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            Page<Transaction> transactions = transactionService.getUserTransactions(authentication.getName(), page, size);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取交易记录失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getTransactionById(@PathVariable String id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            if (transaction == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ApiResponse.success(transaction));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取交易详情失败: " + e.getMessage()));
        }
    }
} 