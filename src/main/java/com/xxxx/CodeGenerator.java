package com.xxxx;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {

    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        list.add("sys_menu");
        list.add("sys_role");
        list.add("sys_role_menu");
        list.add("sys_user");
        list.add("sys_user_role");

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/vueadmin?useUnicode=true&useSSL=false&characterEncoding=utf-8&serverTimezone=Asia/Shanghai", "root", "1234")
                .globalConfig(builder -> {
                    builder.author("chao") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\b\\code\\spring+vue\\demo\\vueadmin-java\\src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.xxxx") // 设置父包名
                            //.moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D:\\b\\code\\spring+vue\\demo\\vueadmin-java\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude(list) // 设置需要生成的表名
                            .addTablePrefix("sys_") // 设置过滤表前缀
                            .entityBuilder()
                            .enableLombok()
                            .mapperBuilder().enableBaseResultMap()
                            .serviceBuilder().formatServiceFileName("%sService")
                            ;
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }
}
