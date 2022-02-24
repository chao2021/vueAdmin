package com.xxxx.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxxx.entity.vo.MenuVo;
import com.xxxx.entity.po.Menu;
import com.xxxx.entity.dto.MenuDto;
import com.xxxx.entity.po.User;
import com.xxxx.mapper.MenuMapper;
import com.xxxx.mapper.RoleMenuMapper;
import com.xxxx.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.service.UserService;
import com.xxxx.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chao
 * @since 2022-02-22
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<MenuVo> getCurrentUserNav() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getByUsername(username);

        List<Long> menuIds = roleMenuMapper.menuIdsByUserId(user.getId());

        List<MenuDto> menuDtos = buildTreeMenu(this.listByIds(menuIds));

        List<MenuVo> menuVos = convert(menuDtos);

        return menuVos;
    }

    @Override
    public List<MenuDto> tree() {
        // 获取所有菜单信息
        List<Menu> menus = this.list(Wrappers.<Menu>lambdaQuery().orderByAsc(Menu::getOrderNum));
        // 转为树状结构
        List<MenuDto> menuDtos = buildTreeMenu(menus);
        return menuDtos;
    }









    // 转换成视图对象Vo
    private List<MenuVo> convert(List<MenuDto> menuDtos) {
        ArrayList<MenuVo> menuVos = new ArrayList<>();

        menuDtos.forEach(menuDto -> {
            MenuVo menuVo = new MenuVo();
            menuVo.setId(menuDto.getId());
            menuVo.setName(menuDto.getPerms());
            menuVo.setTitle(menuDto.getName());
            menuVo.setIcon(menuDto.getIcon());
            menuVo.setPath(menuDto.getPath());
            menuVo.setComponent(menuDto.getComponent());
            // 递归查询所有子节点
            if (menuDto.getChildren().size() > 0) {
                menuVo.setChildren(convert(menuDto.getChildren()));
            }
            menuVos.add(menuVo);
        });
        return menuVos;
    }

    // 转为树形结构
    private List<MenuDto> buildTreeMenu(List<Menu> menus) {
        List<MenuDto> finalMenus = new ArrayList<>();
        // 转换为数据传输对象Dto
        List<MenuDto> menuDtos = BeanCopyUtil.copyList(menus, MenuDto.class);

        for (MenuDto menuDto : menuDtos) {
            // 找出节点的parentId，并把该节点存入父节点的children属性
            for (MenuDto children : menuDtos) {
                if (children.getParentId().equals(menuDto.getId())) {
                    menuDto.getChildren().add(children);
                }
            }
            // 节点的parentId为0时，说明该节点没有上级
            if (menuDto.getParentId().equals(0L)) {
                finalMenus.add(menuDto);
            }
        }

        return finalMenus;
    }
}
