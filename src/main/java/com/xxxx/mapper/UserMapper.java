package com.xxxx.mapper;

import com.xxxx.entity.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author chao
 * @since 2022-02-22
 */
public interface UserMapper extends BaseMapper<User> {

    User getByUsername(@Param("username") String username);

    List<User> listByMenuId(@Param("menuId") Long menuId);

    List<User> listByRoleId(@Param("roleId") Long roleId);
}
