package com.xxxx.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxxx.entity.dto.MenuDto;
import com.xxxx.entity.po.Menu;
import com.xxxx.entity.po.RoleMenu;
import com.xxxx.entity.vo.MenuVo;
import com.xxxx.common.lang.Result;
import com.xxxx.entity.po.User;
import com.xxxx.service.MenuService;
import com.xxxx.service.RoleMenuService;
import com.xxxx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sys/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleMenuService roleMenuService;

    // 获取当前角色的权限以及对应的导航栏信息
    @GetMapping("/nav")
    public Result nav(Principal principal) {
        String username = principal.getName();
        User user = userService.getByUsername(username);
        String authorityInfo = userService.getAuthorityInfo(user.getId());
        String[] authorityInfoArray = StringUtils.tokenizeToStringArray(authorityInfo, ",");

        List<MenuVo> navs = menuService.getCurrentUserNav();

        return Result.success(MapUtil.builder()
                .put("authoritys", authorityInfoArray)
                .put("nav", navs)
                .map()
        );
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result info(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result list() {
        List<MenuDto> menus = menuService.tree();
        return Result.success(menus);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result save(@Validated @RequestBody Menu menu) {
        menu.setCreated(LocalDateTime.now());
        if (StringUtils.hasText(String.valueOf(menu.getStatu()))) {
            menu.setStatu(1);
        }
        menuService.save(menu);
        return Result.success();
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result update(@Validated @RequestBody Menu menu) {
        menu.setUpdated(LocalDateTime.now());
        menuService.updateById(menu);

        userService.clearUserAuthorityInfoByMenuId(menu.getId());
        return Result.success();
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable Long id) {
        // 判断删除的菜单是否由子菜单
        long count = menuService.count(Wrappers.<Menu>lambdaQuery().eq(Menu::getParentId, id));
        if (count > 0) {
            return Result.fail("请先删除子菜单");
        }
        // 清楚所有该菜单的权限缓存
        userService.clearUserAuthorityInfoByMenuId(id);

        menuService.removeById(id);
        // 同步需删除sys_role_menu中间表的相关记录
        roleMenuService.remove(Wrappers.<RoleMenu>lambdaQuery().eq(RoleMenu::getMenuId, id));

        return Result.success();
    }

}
