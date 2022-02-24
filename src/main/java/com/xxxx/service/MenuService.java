package com.xxxx.service;

import com.xxxx.entity.dto.MenuDto;
import com.xxxx.entity.vo.MenuVo;
import com.xxxx.entity.po.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chao
 * @since 2022-02-22
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> getCurrentUserNav();

    List<MenuDto> tree();

}
