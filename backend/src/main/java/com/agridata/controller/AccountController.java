package com.agridata.controller;

import com.agridata.dto.ApiResponse;
import com.agridata.model.Transaction;
import com.agridata.model.User;
import com.agridata.service.AccountService;
import com.agridata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    /**
     * 获取账户余额信息
     */
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<User.BalanceInfo>> getBalance(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            User user = userService.getCurrentUser(authentication.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }

            User.BalanceInfo balanceInfo = accountService.getBalanceInfo(user.getId());
            return ResponseEntity.ok(ApiResponse.success(balanceInfo));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取余额失败: " + e.getMessage()));
        }
    }

    /**
     * 充值
     */
    @PostMapping("/recharge")
    public ResponseEntity<ApiResponse<Transaction>> recharge(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            Double amount = Double.valueOf(request.get("amount").toString());
            String paymentMethod = request.getOrDefault("paymentMethod", "支付宝").toString();

            if (amount <= 0) {
                return ResponseEntity.badRequest().body(ApiResponse.error("充值金额必须大于0"));
            }

            if (amount > 10000) {
                return ResponseEntity.badRequest().body(ApiResponse.error("单次充值金额不能超过10000元"));
            }

            User user = userService.getCurrentUser(authentication.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }

            Transaction transaction = accountService.recharge(user.getId(), amount, paymentMethod);
            return ResponseEntity.ok(ApiResponse.success("充值成功", transaction));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("充值失败: " + e.getMessage()));
        }
    }

    /**
     * 检查购买能力
     */
    @PostMapping("/check-purchase")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPurchaseAbility(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            Double amount = Double.valueOf(request.get("amount").toString());
            User user = userService.getCurrentUser(authentication.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }

            User.BalanceInfo balanceInfo = accountService.getBalanceInfo(user.getId());
            Map<String, Object> result = Map.of(
                "canPurchase", balanceInfo.getAvailableBalance() >= amount,
                "currentBalance", balanceInfo.getAvailableBalance(),
                "requiredAmount", amount,
                "shortfall", Math.max(0, amount - balanceInfo.getAvailableBalance())
            );

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("检查购买能力失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户信息（包含余额）
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }

            User user = userService.getCurrentUser(authentication.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
            }

            User.BalanceInfo balanceInfo = accountService.getBalanceInfo(user.getId());
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone() != null ? user.getPhone() : "");
            userInfo.put("location", user.getLocation() != null ? user.getLocation() : "");
            userInfo.put("bio", user.getBio() != null ? user.getBio() : "");
            userInfo.put("role", user.getRole());
            userInfo.put("status", user.getStatus());
            userInfo.put("balance", balanceInfo);
            userInfo.put("createTime", user.getCreateTime());
            userInfo.put("lastLoginTime", user.getLastLoginTime());

            return ResponseEntity.ok(ApiResponse.success(userInfo));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取用户信息失败: " + e.getMessage()));
        }
    }
} 