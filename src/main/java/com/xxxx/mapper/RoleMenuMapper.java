package com.xxxx.mapper;

import com.xxxx.entity.po.RoleMenu;
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
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    List<Long> menuIdsByUserId(@Param("userId") Long userId);
}
