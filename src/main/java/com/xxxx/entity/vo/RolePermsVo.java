package com.xxxx.entity.vo;

import com.xxxx.entity.po.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RolePermsVo extends Role {

    private List<Long> menuIds = new ArrayList<>();

}
