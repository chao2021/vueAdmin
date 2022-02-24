package com.xxxx.mapper;

import com.xxxx.entity.po.Role;
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
public interface RoleMapper extends BaseMapper<Role> {


    List<Role> listByUserId(@Param("userId") Long userId);
}
