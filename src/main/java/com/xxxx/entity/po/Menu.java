package com.xxxx.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author chao
 * @since 2022-02-22
 */
@Getter
@Setter
@TableName("sys_menu")
@ApiModel(value = "Menu对象", description = "")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("父菜单ID，一级菜单为0")
    @NotNull(message = "上级菜单不能为空")
    private Long parentId;

    @NotBlank(message = "菜单名不为空")
    private String name;

    @ApiModelProperty("菜单URL")
    private String path;

    @ApiModelProperty("授权(多个用逗号分隔，如：user:list,user:create)")
    @NotBlank(message = "菜单授权码不为空")
    private String perms;

    private String component;

    @ApiModelProperty("类型     0：目录   1：菜单   2：按钮")
    @NotNull(message = "菜单类型不为空")
    private Integer type;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("排序")
    private Integer orderNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updated;

    private Integer statu;


}
