package com.xxxx.entity.dto;

import com.xxxx.entity.po.Menu;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class MenuDto extends Menu {

    List<MenuDto> children = new ArrayList<>();
}
