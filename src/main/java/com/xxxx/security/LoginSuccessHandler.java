package com.xxxx.security;

import cn.hutool.json.JSONUtil;
import com.xxxx.common.lang.Result;
import com.xxxx.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        // 生成jwt，并放置到请求头中
        String jwt = jwtUtil.createToken(authentication.getName());
        response.setHeader(jwtUtil.getHeader(), jwt);

        Result result = Result.success();

        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }
}
