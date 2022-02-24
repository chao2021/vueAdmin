package com.xxxx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxxx.entity.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chao
 * @since 2022-02-22
 */
public interface UserService extends IService<User> {

    User getByUsername(String username);

    String getAuthorityInfo(Long userId);

    void clearUserAuthorityInfoByMenuId(Long menuId);

    void clearUserAuthorityInfoByRoleId(Long id);

    IPage<User> page(Integer current, Integer size, String username);
}
