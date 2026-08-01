package com.ruoyi.shebao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当前自然月业务期已存在未删除支付计划时，禁止执行该方法（待遇相关写操作）。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BlockIfCurrentMonthPaymentPlan
{
}
