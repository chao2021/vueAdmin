package com.xxxx.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxxx.common.exception.RegisterException;
import com.xxxx.common.lang.Constants;
import com.xxxx.common.lang.Result;
import com.xxxx.entity.dto.PassDto;
import com.xxxx.entity.po.UserRole;
import com.xxxx.entity.vo.UserVo;
import com.xxxx.entity.po.User;
import com.xxxx.service.RoleService;
import com.xxxx.service.UserRoleService;
import com.xxxx.service.UserService;
import com.xxxx.utils.BeanCopyUtil;
import com.xxxx.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api(tags = "用户")
@RestController
@RequestMapping("/sys/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @ApiOperation("根据用户id查询信息")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id) {
        User user = userService.getById(id);
        Assert.notNull(user, "找不到用户");
        UserVo userVo = BeanCopyUtil.copyProperties(user, UserVo.class);

        userVo.setRoles(roleService.listByUserId(id));

        return Result.success(userVo);
    }
//<el-tag size="small" type="info" v-for="item in scope.row.roles">{{item.name}}</el-tag>
    @ApiOperation("分页查询用户，可以通过用户名模糊查询")
    @GetMapping("/list")
    public Result list(@RequestParam(value = "current",defaultValue = "1") Integer current,
                       @RequestParam(value = "size",defaultValue = "5") Integer size,
                       String username) {
        IPage<User> page = userService.page(current, size, username);
        return Result.success(page);
    }

    @ApiOperation("添加用户")
    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user) {

        User temp = userService.getByUsername(user.getUsername());
        if (temp != null) {
            throw new RegisterException("用户名已存在");
        }

        user.setStatu(Constants.STATU_ON);
        user.setCreated(LocalDateTime.now());
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));

        userService.save(user);
        return Result.success();
    }

    @ApiOperation("编辑，更新用户")
    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        user.setUpdated(LocalDateTime.now());

        userService.updateById(user);

        return Result.success();
    }

    @ApiOperation("批量删除用户")
    @PostMapping("/delete/{ids}")
    @Transactional
    public Result delete(@PathVariable @RequestBody Long[] ids) {

        userService.removeBatchByIds(Arrays.asList(ids));
        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().in(UserRole::getUserId, ids));
        return Result.success();
    }

    @ApiOperation("根据用户id分配角色")
    @PostMapping("/perm/{id}")
    public Result permRole(@PathVariable Long id, @RequestBody Long[] roleIds) {
        List<UserRole> userRoles = new ArrayList<>();

        Arrays.stream(roleIds).forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        });

        // 先删除用户原有的角色
        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, id));

        userRoleService.saveBatch(userRoles);

        // 删除缓存
        redisUtil.del("GrantedAuthority:" + userService.getById(id).getUsername());

        return Result.success();
    }

    @ApiOperation("重置密码")
    @PostMapping("/repass/{id}")
    public Result repass(@PathVariable Long id) {
        User user = userService.getById(id);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdated(LocalDateTime.now());

        userService.updateById(user);

        return Result.success();
    }

    @ApiOperation("修改密码")
    @PostMapping("/updatePass")
    public Result repass(@Validated @RequestBody PassDto passDto, Principal principal) {
        String username = principal.getName();
        User user = userService.getByUsername(username);
        boolean matches = passwordEncoder.matches(passDto.getPassword(), user.getPassword());
        if (!matches) {
            return Result.fail("原密码错误");
        }
        String encode = passwordEncoder.encode(passDto.getCurrentPass());
        user.setPassword(encode);
        user.setUpdated(LocalDateTime.now());

        userService.updateById(user);

        return Result.success();
    }
}
