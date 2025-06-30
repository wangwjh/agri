package com.agridata.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    
    @Indexed
    private String assetId; // 资产ID
    
    private String assetName; // 资产名称
    
    @Indexed
    private String buyerId; // 买家ID
    
    private String buyerName; // 买家名称
    
    @Indexed
    private String sellerId; // 卖家ID
    
    private String sellerName; // 卖家名称
    
    private Double price; // 交易价格
    
    @Indexed
    private TransactionType type = TransactionType.PURCHASE; // 交易类型
    
    @Indexed
    private TransactionStatus status = TransactionStatus.PENDING; // 交易状态
    
    @Indexed
    private LocalDateTime transactionTime = LocalDateTime.now();
    
    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime = LocalDateTime.now();
    
    // 交易描述/备注
    private String description;
    
    // 手续费
    private Double fee = 0.0;
    
    // 实际支付金额（价格 + 手续费）
    private Double totalAmount;
    
    // 交易类型枚举
    public enum TransactionType {
        PURCHASE("购买"),
        RECHARGE("充值"),
        WITHDRAW("提现"),
        REFUND("退款"),
        COMMISSION("佣金");
        
        private final String description;
        
        TransactionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 交易状态枚举
    public enum TransactionStatus {
        PENDING("待处理"),
        PROCESSING("处理中"),
        COMPLETED("已完成"),
        FAILED("失败"),
        CANCELLED("已取消"),
        REFUNDED("已退款");
        
        private final String description;
        
        TransactionStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 