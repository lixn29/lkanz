package com.lxn.lkanz.entity;

import lombok.Data;

/**
 * @Author: lxn
 * @Date: 2019/12/25 13:20
 * @Version 1.0
 */
@Data
public class User extends BaseModel {
    private String username;
    private String password;
}
