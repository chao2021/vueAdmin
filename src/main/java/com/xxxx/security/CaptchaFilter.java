package com.xxxx.security;

import cn.hutool.core.util.StrUtil;
import com.xxxx.common.exception.CaptchaException;
import com.xxxx.common.lang.Constants;
import com.xxxx.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        String method = request.getMethod();
        if ("/login".equals(url) && method.equals("POST")) {
            try {
                validate(request);
            } catch (CaptchaException e) {
                loginFailureHandler.onAuthenticationFailure(request, response, e);
            }
        }
        filterChain.doFilter(request, response);
    }

    // 根据token（key）从redis获取验证码
    private void validate(HttpServletRequest request) {
        String code = request.getParameter("code");
        String key = request.getParameter("token");

        if (StrUtil.isBlank(code) || StrUtil.isBlank(key)) {
            throw new CaptchaException("验证码错误");
        }
        if (!code.equals(redisUtil.hget(Constants.CAPTCHA_KEY, key))) {
            throw new CaptchaException("验证码有误");
        }
    }
}
