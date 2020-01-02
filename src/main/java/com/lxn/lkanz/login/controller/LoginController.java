package com.lxn.lkanz.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: lxn
 * @Date: 2019/12/18 14:08
 * @Version 1.0
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/")
    public String login() {
        return "index";
    }
}
