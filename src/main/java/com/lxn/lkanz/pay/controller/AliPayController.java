package com.lxn.lkanz.pay.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lxn
 * @Date: 2020/3/5 14:42
 * @Version 1.0
 */
@RestController
public class AliPayController {
    @RequestMapping(value = "/lxn/api/aliPay")
    public String aliPay(){

        return "";
    }
}
