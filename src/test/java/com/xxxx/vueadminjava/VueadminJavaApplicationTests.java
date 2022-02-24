package com.xxxx.vueadminjava;

import com.xxxx.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VueadminJavaApplicationTests {

    @Autowired
    JwtUtil jwtUtil;

    @Test
    void contextLoads() {//SignatureException
    }

    @Test
    void jwtUtilTest() {
        Claims claims = jwtUtil.getClaimByToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY0NTYzMjMxOSwiZXhwIjoxNjQ1NjMyMzc5fQ.V6msuBk4BQoCbBN2tTqGqmF50K8wRAJRR2IgkexNRY8");
    }
}
