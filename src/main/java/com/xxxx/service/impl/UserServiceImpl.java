package com.xxxx.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxxx.entity.po.Menu;
import com.xxxx.entity.po.Role;
import com.xxxx.entity.po.User;
import com.xxxx.mapper.*;
import com.xxxx.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    public String getAuthorityInfo(Long userId) {

        User user = baseMapper.selectById(userId);

        String authority = "";
        if (redisUtil.hasKey("GrantedAuthority" + user.getUsername())) {
            authority = (String) redisUtil.get("GrantedAuthority" + user.getUsername());
        } else {
            List<Integer> roleIds = userRoleMapper.roleIdsByUserId(userId);
            //
            if (CollectionUtils.isEmpty(roleIds)) {
                return null;
            }
            List<Role> roles = roleMapper.selectBatchIds(roleIds);
            // 获取角色编码
            if (!CollectionUtils.isEmpty(roles)) {
                // 必须要加前缀：ROLE_
                String roleCodes = roles.stream().map(role -> "ROLE_" + role.getCode()).collect(Collectors.joining(","));
                authority = roleCodes.concat(",");
            }
            List<Long> menuIds = roleMenuMapper.menuIdsByUserId(userId);
            List<Menu> menus = menuMapper.selectBatchIds(menuIds);
            // 获取目录编码
            String menuPerms = menus.stream().map(Menu::getPerms).collect(Collectors.joining(","));
            authority = authority.concat(menuPerms);
        }
        redisUtil.set("GrantedAuthority:" + user.getUsername(), authority, 60 * 60);

        return authority;
    }

    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        List<User> users = userMapper.listByMenuId(menuId) ;

        this.clearUserAuthorityInfo(users);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
        List<User> users = userMapper.listByRoleId(roleId);
        this.clearUserAuthorityInfo(users);
    }

    @Override
    public IPage<User> page(Integer current, Integer size, String username) {
        Page<User> userPage = new Page<>(current, size);
        return this.page(userPage, Wrappers.<User>lambdaQuery().like(StrUtil.isNotBlank(username), User::getUsername, userPage));
    }

    // 删除缓存
    private void clearUserAuthorityInfo(List<User> users) {
        List<String> usernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        usernames.forEach(username -> redisUtil.del("GrantedAuthority:" + username));

    }

}
