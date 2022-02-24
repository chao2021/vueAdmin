package com.xxxx.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.google.code.kaptcha.Producer;
import com.xxxx.common.lang.Constants;
import com.xxxx.common.lang.Result;
import com.xxxx.entity.po.User;
import com.xxxx.entity.vo.UserInfoVo;
import com.xxxx.service.UserService;
import com.xxxx.utils.BeanCopyUtil;
import com.xxxx.utils.RedisUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;

@RestController
public class AuthController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Producer producer;
    @Autowired
    private UserService userService;

    @GetMapping(value = "/captcha")
    @ApiOperation("验证码")
    public Result captcha() throws IOException {
        String code = producer.createText();
        String key = IdUtil.fastSimpleUUID();

        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ImageIO.write(image, "jpg", outputStream);

        BASE64Encoder encoder = new BASE64Encoder();
        String str = "data:image/jpeg;base64,";
        String base64Img = str + encoder.encode(outputStream.toByteArray());

        // 将验证码信息存入redis
        redisUtil.hset(Constants.CAPTCHA_KEY, key, code, 120);

        return Result.success(MapUtil.builder()
                .put("token", key)
                .put("captchaImg", base64Img)
                .build());
    }

    @GetMapping("/sys/userInfo")
    public Result UserInfo(Principal principal) {
        User user = userService.getByUsername(principal.getName());

        return Result.success(BeanCopyUtil.copyProperties(user, UserInfoVo.class));
    }

}
