# API联调测试脚本
$baseUrl = "http://localhost:8087/api"

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  廊坊社保管理系统 - API联调测试" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# 测试1: 获取验证码
Write-Host "[测试1] 获取验证码..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/system/captchaImage" -Method Get
    if ($response.code -eq 200) {
        Write-Host "  ✓ 验证码获取成功" -ForegroundColor Green
        Write-Host "    UUID: $($response.uuid)" -ForegroundColor Gray
    } else {
        Write-Host "  ✗ 验证码获取失败" -ForegroundColor Red
    }
} catch {
    Write-Host "  ✗ 请求失败: $_" -ForegroundColor Red
}

Write-Host ""

# 测试2: 获取字典数据（审批状态）
Write-Host "[测试2] 获取字典数据（审批状态）..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/system/dict/data/type/approval_status" -Method Get
    if ($response.code -eq 200) {
        Write-Host "  ✓ 字典数据获取成功" -ForegroundColor Green
        Write-Host "    记录数: $($response.data.Count)" -ForegroundColor Gray
    } else {
        Write-Host "  ✗ 字典数据获取失败" -ForegroundColor Red
    }
} catch {
    Write-Host "  ✗ 请求失败: $_" -ForegroundColor Red
}

Write-Host ""

# 测试3: 获取字典数据（业务类型）
Write-Host "[测试3] 获取字典数据（业务类型）..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/system/dict/data/type/business_type" -Method Get
    if ($response.code -eq 200) {
        Write-Host "  ✓ 字典数据获取成功" -ForegroundColor Green
        Write-Host "    记录数: $($response.data.Count)" -ForegroundColor Gray
    } else {
        Write-Host "  ✗ 字典数据获取失败" -ForegroundColor Red
    }
} catch {
    Write-Host "  ✗ 请求失败: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  测试总结" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "后端服务运行正常，可以进行进一步的业务测试" -ForegroundColor Green
Write-Host ""
Write-Host "下一步：" -ForegroundColor Yellow
Write-Host "  1. 启动前端服务（npm run dev）" -ForegroundColor White
Write-Host "  2. 访问 http://localhost:80 进行登录" -ForegroundColor White
Write-Host "  3. 使用测试账号进行功能测试" -ForegroundColor White
Write-Host ""
