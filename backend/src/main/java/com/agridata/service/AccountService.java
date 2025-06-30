package com.agridata.service;

import com.agridata.model.Transaction;
import com.agridata.model.User;
import com.agridata.repository.TransactionRepository;
import com.agridata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 获取用户余额信息
     */
    public User.BalanceInfo getBalanceInfo(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        
        return new User.BalanceInfo(
            user.getBalance(),
            user.getFrozenBalance(),
            user.getBalance() - user.getFrozenBalance()
        );
    }

    /**
     * 充值
     */
    @Transactional
    public Transaction recharge(String userId, Double amount, String paymentMethod) {
        if (amount <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 创建充值交易记录
        Transaction transaction = new Transaction();
        transaction.setBuyerId(userId);
        transaction.setBuyerName(user.getUsername());
        transaction.setPrice(amount);
        transaction.setTotalAmount(amount);
        transaction.setType(Transaction.TransactionType.RECHARGE);
        transaction.setStatus(Transaction.TransactionStatus.PROCESSING);
        transaction.setDescription("账户充值 - " + paymentMethod);
        transaction.setTransactionTime(LocalDateTime.now());

        // 保存交易记录
        transaction = transactionRepository.save(transaction);

        // 模拟支付成功，直接完成充值
        completeRecharge(transaction.getId());

        return transaction;
    }

    /**
     * 完成充值
     */
    @Transactional
    public void completeRecharge(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction == null || transaction.getType() != Transaction.TransactionType.RECHARGE) {
            throw new IllegalArgumentException("无效的充值交易");
        }

        User user = userRepository.findById(transaction.getBuyerId()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 增加用户余额
        user.setBalance(user.getBalance() + transaction.getPrice());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);

        // 更新交易状态
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setUpdateTime(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    /**
     * 冻结余额（用于预扣款）
     */
    @Transactional
    public boolean freezeBalance(String userId, Double amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // 检查余额是否足够
        Double availableBalance = user.getBalance() - user.getFrozenBalance();
        if (availableBalance < amount) {
            return false;
        }

        // 冻结余额
        user.setFrozenBalance(user.getFrozenBalance() + amount);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    /**
     * 解冻余额
     */
    @Transactional
    public void unfreezeBalance(String userId, Double amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        user.setFrozenBalance(Math.max(0, user.getFrozenBalance() - amount));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 扣除余额（从冻结余额中扣除）
     */
    @Transactional
    public boolean deductBalance(String userId, Double amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // 检查冻结余额是否足够
        if (user.getFrozenBalance() < amount) {
            return false;
        }

        // 从冻结余额和总余额中扣除
        user.setFrozenBalance(user.getFrozenBalance() - amount);
        user.setBalance(user.getBalance() - amount);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    /**
     * 增加余额（收入）
     */
    @Transactional
    public void addBalance(String userId, Double amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        user.setBalance(user.getBalance() + amount);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 转账（从买家到卖家）
     */
    @Transactional
    public boolean transferBalance(String fromUserId, String toUserId, Double amount, String reason) {
        // 从买家扣除余额
        if (!deductBalance(fromUserId, amount)) {
            return false;
        }

        // 给卖家增加余额
        addBalance(toUserId, amount);

        return true;
    }
} 