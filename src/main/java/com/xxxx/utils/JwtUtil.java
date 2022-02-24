package com.xxxx.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtUtil {

    private Integer expire;
    private String secret;
    private String header;

    public String createToken(String username) {

        Date now = new Date(System.currentTimeMillis());
        DateTime expireDate = DateUtil.offsetMinute(now, expire);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // 解析jwt
    public Claims getClaimByToken(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();
    }

    // jwt是否过期
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }


}
