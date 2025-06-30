# 错误处理控制器使用说明

## 概述

本系统新增了一个强大的错误处理控制器 `ErrorHandlerController`，提供了全局异常处理和调试功能，帮助开发者快速定位和解决问题。

## 主要功能

### 1. 全局异常处理

系统会自动捕获以下类型的异常并返回友好的错误信息：

- **参数校验异常** (`MethodArgumentNotValidException`)
- **约束违反异常** (`ConstraintViolationException`)
- **JSON解析异常** (`HttpMessageNotReadableException`)
- **认证失败异常** (`BadCredentialsException`)
- **权限不足异常** (`AccessDeniedException`)
- **认证不足异常** (`InsufficientAuthenticationException`)
- **请求方法不支持异常** (`HttpRequestMethodNotSupportedException`)
- **404异常** (`NoHandlerFoundException`)
- **其他未捕获异常** (`Exception`)

### 2. 调试接口

#### 健康检查接口
```
GET /api/debug/health
```

**响应示例：**
```json
{
  "success": true,
  "message": "系统正常运行",
  "data": {
    "status": "UP",
    "timestamp": "2024-01-01T12:00:00",
    "version": "1.0.0",
    "environment": "default"
  }
}
```

#### 系统信息接口
```
GET /api/debug/info
```

**响应示例：**
```json
{
  "success": true,
  "message": "系统信息",
  "data": {
    "javaVersion": "17.0.1",
    "maxMemory": "1024 MB",
    "totalMemory": "512 MB",
    "freeMemory": "256 MB",
    "processors": 8,
    "osName": "Windows 10",
    "osVersion": "10.0",
    "timestamp": "2024-01-01T12:00:00"
  }
}
```

#### 错误测试接口
```
POST /api/debug/test-error?type=[runtime|null|illegal]
```

**参数说明：**
- `runtime`: 测试运行时异常
- `null`: 测试空指针异常  
- `illegal`: 测试非法参数异常

**使用示例：**
```bash
# 测试运行时异常
curl -X POST "http://localhost:8080/api/debug/test-error?type=runtime"

# 测试空指针异常
curl -X POST "http://localhost:8080/api/debug/test-error?type=null"

# 测试非法参数异常
curl -X POST "http://localhost:8080/api/debug/test-error?type=illegal"
```

## 错误响应格式

所有异常都会返回统一的错误响应格式：

```json
{
  "success": false,
  "message": "错误描述信息",
  "data": {
    "message": "详细错误信息",
    "type": "异常类型",
    "timestamp": "发生时间",
    "path": "请求路径",
    "fieldErrors": {
      "字段名": "错误信息"
    }
  }
}
```

## 日志功能

### 1. 启动时调试信息

应用启动时会在控制台输出详细的系统信息：

```
============================================================
农业数据平台应用启动完成
============================================================
启动时间: 2024-01-01T12:00:00
JVM版本: 17.0.1
操作系统: Windows 10 10.0
活动配置文件: [default]
服务端口: 8080
调试接口:
  健康检查: GET /api/debug/health
  系统信息: GET /api/debug/info
  错误测试: POST /api/debug/test-error?type=[runtime|null|illegal]
内存信息:
  最大内存: 1024 MB
  总内存: 512 MB
  可用内存: 256 MB
  处理器核心数: 8
============================================================
```

### 2. HTTP请求日志

系统会自动记录所有API请求的详细信息：

**请求开始日志：**
```
=== HTTP请求开始 ===
时间: 2024-01-01T12:00:00
方法: POST
URL: http://localhost:8080/api/auth/login
查询参数: null
远程地址: 127.0.0.1
用户代理: Mozilla/5.0 ...
请求头:
  Content-Type: application/json
  Authorization: ***HIDDEN***
=================
```

**请求完成日志：**
```
=== HTTP请求完成 ===
时间: 2024-01-01T12:00:01
方法: POST
URL: http://localhost:8080/api/auth/login
状态码: 200
处理时间: 125 ms
=================
```

## 调试技巧

### 1. 快速健康检查
```bash
curl http://localhost:8080/api/debug/health
```

### 2. 查看系统资源使用情况
```bash
curl http://localhost:8080/api/debug/info
```

### 3. 测试异常处理机制
```bash
# 触发运行时异常
curl -X POST "http://localhost:8080/api/debug/test-error?type=runtime"
```

### 4. 查看应用日志
启动应用后，查看控制台输出的详细日志信息，包括：
- 系统启动信息
- HTTP请求详情
- 异常堆栈信息

## 注意事项

1. **生产环境**: 请在生产环境中适当调整日志级别，避免过多的调试信息影响性能
2. **安全性**: 敏感信息（如Authorization头）会被自动隐藏
3. **性能**: 请求日志拦截器只对 `/api/**` 路径生效，不会影响静态资源访问
4. **测试**: 错误测试接口仅用于开发调试，生产环境中建议禁用或限制访问

## 故障排除

如果遇到问题，可以按以下步骤排查：

1. 检查健康检查接口是否正常响应
2. 查看系统信息接口获取当前系统状态
3. 查看控制台日志获取详细错误信息
4. 使用错误测试接口验证异常处理机制
5. 检查HTTP请求日志分析请求流程 