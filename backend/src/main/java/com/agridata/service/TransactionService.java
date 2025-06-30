package com.agridata.service;

import com.agridata.model.AgriculturalAsset;
import com.agridata.model.Transaction;
import com.agridata.model.User;
import com.agridata.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AgriculturalAssetService assetService;

    @Autowired
    private AccountService accountService;

    /**
     * 创建购买交易
     */
    @Transactional
    public Transaction createPurchaseTransaction(String assetId, String buyerUsername) {
        System.out.println("开始创建购买交易 - 资产ID: " + assetId + ", 买家: " + buyerUsername);
        
        // 获取买家信息
        User buyer = userService.getCurrentUser(buyerUsername);
        if (buyer == null) {
            System.out.println("购买失败：买家不存在 - " + buyerUsername);
            throw new IllegalArgumentException("买家不存在");
        }

        // 获取资产信息
        AgriculturalAsset asset = assetService.getAssetById(assetId);
        if (asset == null) {
            System.out.println("购买失败：资产不存在 - " + assetId);
            throw new IllegalArgumentException("资产不存在");
        }

        // 检查资产状态
        if (!"AVAILABLE".equals(asset.getStatus()) || 
            asset.getReviewStatus() != AgriculturalAsset.ReviewStatus.APPROVED ||
            asset.getPublishStatus() != AgriculturalAsset.PublishStatus.PUBLISHED) {
            System.out.println("购买失败：资产不可购买 - 状态: " + asset.getStatus() + 
                             ", 审核状态: " + asset.getReviewStatus() + 
                             ", 发布状态: " + asset.getPublishStatus());
            throw new IllegalArgumentException("资产不可购买");
        }

        // 不能购买自己的资产
        if (asset.getOwnerId().equals(buyer.getId())) {
            System.out.println("购买失败：不能购买自己的资产");
            throw new IllegalArgumentException("不能购买自己的资产");
        }

        // 获取卖家信息
        User seller = userService.getUserById(asset.getOwnerId());
        if (seller == null) {
            System.out.println("购买失败：卖家不存在 - " + asset.getOwnerId());
            throw new IllegalArgumentException("卖家不存在");
        }

        // 计算交易金额
        Double price = asset.getPrice();
        Double fee = price * 0.05; // 5% 手续费
        Double totalAmount = price + fee;

        System.out.println("交易金额计算 - 商品价格: " + price + ", 手续费: " + fee + ", 总计: " + totalAmount);

        // 检查买家余额
        User.BalanceInfo balanceInfo = accountService.getBalanceInfo(buyer.getId());
        if (balanceInfo == null || balanceInfo.getAvailableBalance() < totalAmount) {
            System.out.println("购买失败：余额不足 - 需要: " + totalAmount + 
                             ", 可用余额: " + (balanceInfo != null ? balanceInfo.getAvailableBalance() : 0));
            throw new IllegalArgumentException("余额不足，请先充值");
        }

        // 冻结买家余额
        System.out.println("冻结买家余额: " + totalAmount);
        if (!accountService.freezeBalance(buyer.getId(), totalAmount)) {
            System.out.println("购买失败：余额冻结失败");
            throw new IllegalArgumentException("余额冻结失败");
        }

        Transaction savedTransaction = null;
        try {
            // 创建交易记录
            Transaction transaction = new Transaction();
            transaction.setAssetId(asset.getId());
            transaction.setAssetName(asset.getName());
            transaction.setBuyerId(buyer.getId());
            transaction.setBuyerName(buyer.getUsername());
            transaction.setSellerId(seller.getId());
            transaction.setSellerName(seller.getUsername());
            transaction.setPrice(price);
            transaction.setFee(fee);
            transaction.setTotalAmount(totalAmount);
            transaction.setType(Transaction.TransactionType.PURCHASE);
            transaction.setStatus(Transaction.TransactionStatus.PROCESSING);
            transaction.setDescription("购买农业数据: " + asset.getName());
            transaction.setTransactionTime(LocalDateTime.now());

            System.out.println("保存交易记录");
            savedTransaction = transactionRepository.save(transaction);

            // 立即完成交易（简化流程）
            System.out.println("完成交易处理");
            completePurchaseTransaction(savedTransaction.getId());
            
            // 重新获取更新后的交易记录
            Transaction finalTransaction = transactionRepository.findById(savedTransaction.getId()).orElse(savedTransaction);
            System.out.println("购买交易完成 - 交易状态: " + finalTransaction.getStatus());
            
            return finalTransaction;

        } catch (Exception e) {
            System.out.println("交易处理失败，解冻余额: " + e.getMessage());
            // 如果交易创建失败，解冻余额
            accountService.unfreezeBalance(buyer.getId(), totalAmount);
            
            // 如果交易记录已创建，更新为失败状态
            if (savedTransaction != null) {
                try {
                    savedTransaction.setStatus(Transaction.TransactionStatus.FAILED);
                    savedTransaction.setUpdateTime(LocalDateTime.now());
                    transactionRepository.save(savedTransaction);
                } catch (Exception ex) {
                    System.out.println("更新交易状态失败: " + ex.getMessage());
                }
            }
            
            throw e;
        }
    }

    /**
     * 完成购买交易
     */
    @Transactional
    public void completePurchaseTransaction(String transactionId) {
        System.out.println("开始完成购买交易 - 交易ID: " + transactionId);
        
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction == null || transaction.getType() != Transaction.TransactionType.PURCHASE) {
            System.out.println("完成交易失败：无效的购买交易 - " + transactionId);
            throw new IllegalArgumentException("无效的购买交易");
        }

        try {
            System.out.println("扣除买家余额 - 买家ID: " + transaction.getBuyerId() + ", 金额: " + transaction.getTotalAmount());
            // 扣除买家余额
            if (!accountService.deductBalance(transaction.getBuyerId(), transaction.getTotalAmount())) {
                System.out.println("扣除买家余额失败");
                throw new RuntimeException("扣除买家余额失败");
            }

            System.out.println("给卖家增加收入 - 卖家ID: " + transaction.getSellerId() + ", 金额: " + transaction.getPrice());
            // 给卖家增加收入（商品价格，手续费由平台收取）
            accountService.addBalance(transaction.getSellerId(), transaction.getPrice());

            System.out.println("更新资产状态为已售出 - 资产ID: " + transaction.getAssetId());
            // 更新资产状态为已售出
            AgriculturalAsset asset = assetService.getAssetById(transaction.getAssetId());
            if (asset != null) {
                asset.setStatus("SOLD");
                asset.setUpdateTime(LocalDateTime.now());
                assetService.saveAsset(asset);
                System.out.println("资产状态更新完成");
            } else {
                System.out.println("警告：未找到对应的资产记录");
            }

            System.out.println("更新交易状态为已完成");
            // 更新交易状态
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setUpdateTime(LocalDateTime.now());
            transactionRepository.save(transaction);
            
            System.out.println("购买交易完成成功 - 交易ID: " + transactionId);

        } catch (Exception e) {
            System.out.println("完成交易时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            // 如果完成交易失败，更新交易状态为失败并解冻余额
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setUpdateTime(LocalDateTime.now());
            transactionRepository.save(transaction);
            
            System.out.println("解冻买家余额 - 金额: " + transaction.getTotalAmount());
            accountService.unfreezeBalance(transaction.getBuyerId(), transaction.getTotalAmount());
            
            throw e;
        }
    }

    // 兼容旧的方法名
    public Transaction createTransaction(String assetId, String buyerUsername) {
        return createPurchaseTransaction(assetId, buyerUsername);
    }

    /**
     * 获取用户的交易记录
     */
    public Page<Transaction> getUserTransactions(String username, int page, int size) {
        User user = userService.getCurrentUser(username);
        if (user == null) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionTime"));
        return transactionRepository.findByUserTransactions(user.getId(), pageable);
    }

    /**
     * 获取用户的购买记录
     */
    public Page<Transaction> getUserPurchases(String username, int page, int size) {
        User user = userService.getCurrentUser(username);
        if (user == null) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionTime"));
        return transactionRepository.findByBuyerIdAndType(user.getId(), Transaction.TransactionType.PURCHASE, pageable);
    }

    /**
     * 获取用户的销售记录
     */
    public Page<Transaction> getUserSales(String username, int page, int size) {
        User user = userService.getCurrentUser(username);
        if (user == null) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionTime"));
        return transactionRepository.findBySellerIdAndType(user.getId(), Transaction.TransactionType.PURCHASE, pageable);
    }

    /**
     * 获取交易详情
     */
    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId).orElse(null);
    }
} 