package com.xxxx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxxx.entity.po.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.entity.vo.RolePermsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chao
 * @since 2022-02-22
 */
public interface RoleService extends IService<Role> {

    RolePermsVo permsByRoleId(Long id);

    Page<Role> page(Integer current, Integer size, String name);

    void delete(Long[] roleIds);

    void perm(Long roleId, Long[] menuIds);

    List<Role> listByUserId(Long id);
}
