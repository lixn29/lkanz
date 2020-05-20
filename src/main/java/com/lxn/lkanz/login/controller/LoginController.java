package com.lxn.lkanz.login.controller;

import com.lxn.lkanz.pay.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: lxn
 * @Date: 2019/12/18 14:08
 * @Version 1.0
 */
@Controller
public class LoginController extends BaseController {
    /**
     * 重定向地址获取用户code码地址
     */
    String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx17264fb6093e19c7&redirect_uri=http://www.dajingjing.com/wx/code&response_type=code&scope=snsapi_base&state=123&123#wechat_redirect";

    @RequestMapping(value = "/")
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("openid", "ot3R4w4G7srg4vTyAsF3svlrcsYk");
        model.addAttribute("jkccOpenid", "oaynI1EW2tQ5n2X12hcDOONK7smg");
        return "index";
    }

    @ResponseBody
    @RequestMapping(value = "/MP_verify_uqr7dW3GpIZ6oIxj.txt")
    public String mp(HttpServletRequest request, Model model) {
        return "uqr7dW3GpIZ6oIxj";
    }


}
