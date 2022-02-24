package com.xxxx.entity.vo;

import com.xxxx.entity.po.Role;
import com.xxxx.entity.po.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserVo extends User {

    private List<Role> roles = new ArrayList<>();
}
