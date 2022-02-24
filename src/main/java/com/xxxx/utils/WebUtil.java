package com.xxxx.utils;

import cn.hutool.json.JSONUtil;
import com.xxxx.common.lang.Result;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WebUtil {

    public static void renderString (HttpServletResponse response, String json) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            ServletOutputStream outputStream = response.getOutputStream();

            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
