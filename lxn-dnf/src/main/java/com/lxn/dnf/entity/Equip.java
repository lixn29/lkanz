package com.lxn.dnf.entity;

import lombok.Data;

/**
 * 装备
 *
 * @Author: lxn
 * @Date: 2020/4/3 10:11
 * @Version 1.0
 */
@Data
public class Equip extends BaseModel {
    /**
     * 主键
     */
    private String id;
    /**
     * 装备名称
     */
    private String name;
    /**
     * 装备介绍图片
     */
    private String pic;
    /**
     * 套装名称
     */
    private String type;
}
