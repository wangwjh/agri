package com.agridata.repository;

import com.agridata.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    
    // 根据买家ID查找交易记录
    Page<Transaction> findByBuyerId(String buyerId, Pageable pageable);
    
    // 根据卖家ID查找交易记录
    Page<Transaction> findBySellerId(String sellerId, Pageable pageable);
    
    // 查找用户的所有交易记录（买家或卖家）
    @Query("{ $or: [ { 'buyerId': ?0 }, { 'sellerId': ?0 } ] }")
    Page<Transaction> findByUserTransactions(String userId, Pageable pageable);
    
    // 根据资产ID查找交易记录
    List<Transaction> findByAssetId(String assetId);
    
    // 根据状态查找交易记录
    Page<Transaction> findByStatus(Transaction.TransactionStatus status, Pageable pageable);
    
    // 根据交易类型查找交易记录
    Page<Transaction> findByType(Transaction.TransactionType type, Pageable pageable);
    
    // 根据买家ID和交易类型查询
    Page<Transaction> findByBuyerIdAndType(String buyerId, Transaction.TransactionType type, Pageable pageable);
    
    // 根据卖家ID和交易类型查询
    Page<Transaction> findBySellerIdAndType(String sellerId, Transaction.TransactionType type, Pageable pageable);
    
    // 根据用户ID和交易类型查询（买家或卖家）
    @Query("{ $and: [ { $or: [ { 'buyerId': ?0 }, { 'sellerId': ?0 } ] }, { 'type': ?1 } ] }")
    Page<Transaction> findByUserIdAndType(String userId, Transaction.TransactionType type, Pageable pageable);
    
    // 统计查询
    long countByBuyerId(String buyerId);
    long countBySellerId(String sellerId);
    long countByType(Transaction.TransactionType type);
    long countByStatus(Transaction.TransactionStatus status);
} 