package com.lxn.lkanz.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: lxn
 * @Date: 2019/12/25 13:20
 * @Version 1.0
 */
@Data
public class BaseModel {
    protected Integer id;
    protected Date create_time;
    protected Date update_time;
    protected String update_user;
}
