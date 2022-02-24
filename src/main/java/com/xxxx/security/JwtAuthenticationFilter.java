package com.xxxx.security;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xxxx.common.lang.Result;
import com.xxxx.entity.po.User;
import com.xxxx.service.UserService;
import com.xxxx.utils.JwtUtil;
import com.xxxx.utils.WebUtil;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(jwtUtil.getHeader());
        if (StrUtil.isBlankOrUndefined(token)) {
            chain.doFilter(request, response);
            return;
        }

        Claims claims;
        String username = "";

        try {
            claims = jwtUtil.getClaimByToken(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException e) {
            WebUtil.renderString(response, JSONUtil.toJsonStr(Result.fail("token 过期")));
            throw new JwtException("token 过期");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            WebUtil.renderString(response, JSONUtil.toJsonStr(Result.fail("token 无效")));
            throw new JwtException("token 无效");
        }

        User user = userService.getByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, userDetailsService.getGrantedAuthority(user.getId()));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }

}
