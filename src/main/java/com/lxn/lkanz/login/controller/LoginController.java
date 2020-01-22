package com.lxn.lkanz.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: lxn
 * @Date: 2019/12/18 14:08
 * @Version 1.0
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/")
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("openid","ot3R4w4G7srg4vTyAsF3svlrcsYk");
        return "index";
    }
}
