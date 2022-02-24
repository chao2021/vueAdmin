package com.xxxx.controller;

import com.xxxx.common.lang.Constants;
import com.xxxx.common.lang.Result;
import com.xxxx.entity.po.Role;
import com.xxxx.entity.vo.RolePermsVo;
import com.xxxx.service.RoleService;
import com.xxxx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/sys/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;


    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result info(@PathVariable Long id) {
        RolePermsVo rolePermsVo = roleService.permsByRoleId(id);
        return Result.success(rolePermsVo);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result list(@RequestParam(value = "current",defaultValue = "1") Integer current,
                       @RequestParam(value = "size",defaultValue = "5") Integer size,
                       String name) {

        return Result.success(roleService.page(current, size, name));
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result save(@Validated @RequestBody Role role) {
        role.setCreated(LocalDateTime.now());
        role.setStatu(Constants.STATU_ON);

        roleService.save(role);
        return Result.success();
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result update(@Validated @RequestBody Role role) {
        role.setUpdated(LocalDateTime.now());

        roleService.updateById(role);
        // 删除缓存
        userService.clearUserAuthorityInfoByRoleId(role.getId());

        return Result.success();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result delete(@RequestBody Long[] roleIds) {
        roleService.delete(roleIds);
        return Result.success();
    }

    /**
     * 给角色分配菜单权限
     * @param roleId 角色id
     * @param menuIds ids
     */
    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    public Result perm(@PathVariable Long roleId, @RequestBody Long[] menuIds) {
        roleService.perm(roleId, menuIds);

        return Result.success();
    }

}
