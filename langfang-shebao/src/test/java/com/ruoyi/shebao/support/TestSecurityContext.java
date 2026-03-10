package com.ruoyi.shebao.support;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

/**
 * 为单元测试提供最小可用的登录上下文，兼容 SecurityUtils 静态调用。
 */
public final class TestSecurityContext {

    private TestSecurityContext() {
    }

    public static void setUser(String username) {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setDeptId(1L);
        user.setUserName(username);
        user.setPassword("test-password");

        LoginUser loginUser = new LoginUser(1L, 1L, user, Collections.emptySet());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
