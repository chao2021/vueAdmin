package com.xxxx.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxxx.entity.po.UserRole;
import com.xxxx.entity.vo.RolePermsVo;
import com.xxxx.entity.po.Role;
import com.xxxx.entity.po.RoleMenu;
import com.xxxx.mapper.RoleMapper;
import com.xxxx.service.RoleMenuService;
import com.xxxx.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.service.UserRoleService;
import com.xxxx.service.UserService;
import com.xxxx.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chao
 * @since 2022-02-22
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public RolePermsVo permsByRoleId(Long id) {
        Role role = this.getById(id);
        if (role == null) {
            throw new IllegalArgumentException("参数异常");
        }
        List<RoleMenu> roleMenus = roleMenuService.list(Wrappers.<RoleMenu>lambdaQuery()
                .eq(RoleMenu::getRoleId, id));
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
        RolePermsVo rolePermsVo = BeanCopyUtil.copyProperties(role, RolePermsVo.class);
        rolePermsVo.setMenuIds(menuIds);

        return rolePermsVo;
    }

    @Override
    public Page<Role> page(Integer current, Integer size, String name) {
        Page<Role> rolePage = new Page<>(current, size);
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(StrUtil.isNotBlank(name), Role::getName, name);
        return this.page(rolePage, wrapper);
    }

    @Override
    @Transactional
    public void delete(Long[] roleIds) {
        this.removeBatchByIds(Arrays.asList(roleIds));
        // 删除中间表记录
        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().in(UserRole::getRoleId, roleIds));
        roleMenuService.remove(Wrappers.<RoleMenu>lambdaQuery().in(RoleMenu::getRoleId, roleIds));
        // 缓存同步删除
        Arrays.stream(roleIds).forEach(roleId ->
                userService.clearUserAuthorityInfoByRoleId(roleId)
        );
    }

    @Override
    @Transactional
    public void perm(Long roleId, Long[] menuIds) {
        List<RoleMenu> roleMenus = new ArrayList<>();

        Arrays.asList(menuIds).forEach(menuId -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);

            roleMenus.add(roleMenu);
        });

        // 先删除后保存
        roleMenuService.remove(Wrappers.<RoleMenu>lambdaQuery().eq(RoleMenu::getRoleId, roleId));
        roleMenuService.saveBatch(roleMenus);

        // 删除缓存
        userService.clearUserAuthorityInfoByRoleId(roleId);
    }

    @Override
    public List<Role> listByUserId(Long id) {
        return roleMapper.listByUserId(id);
    }
}
