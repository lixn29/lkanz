package com.lxn.lkanz.codetest.controller;

import com.lxn.lkanz.entity.User;
import com.lxn.lkanz.pay.util.HttpUtil;
import com.lxn.lkanz.pay.util.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: lxn
 * @Date: 2019/12/25 14:14
 * @Version 1.0
 */
@Slf4j
@Controller
public class TestController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 数据库连接测试,以jdbcTemplate使用为例
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/code/test")
    public String jdbcTemplateTest() {
        String sql = "select * from sys_user";
        List<User> list = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(User.class));
        for (User user : list) {
            log.info("username={},create_time={}", user.getUsername(), user.getCreate_time());
        }
        return "success";
    }

    @RequestMapping(value = "/callBackUrl")
    @ResponseBody
    public String callBackUrl() {
        String url = "http://172.16.9.184:9004/api.pay.notify/?income=1&payOrderId=P01201912301450279720051&amount=1&mchId=20000000&productId=8020&mchOrderNo=243050868830633984&paySuccTime=2019-12-30+14%3A50%3A34&sign=69AD0824356ACAA8E5A898CA0DB02B11&channelOrderNo=2019123022001400791404869577&backType=2&param1=param1&param2=param2&status=2&payPassAccountId=3";
        String result = null;
        try {
            result = HttpUtil.doPostSSL(url, "");
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        log.info("result = ", result);
        return result;
    }

}
