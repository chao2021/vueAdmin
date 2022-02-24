package com.xxxx.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuVo {

    private Long id;
    private String name;
    private String title;
    private String icon;
    private String component;
    private String path;

    List<MenuVo> children = new ArrayList<>();

}
