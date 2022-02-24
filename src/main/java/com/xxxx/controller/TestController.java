package com.xxxx.controller;

import com.xxxx.common.lang.Result;
import com.xxxx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result test() {
        return Result.success(userService.list());
    }

    @GetMapping("/2")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result test2() {
        return Result.success(userService.list());
    }
}
