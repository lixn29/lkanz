package com.lxn.lkanz.codetest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxn.lkanz.entity.User;
import com.lxn.lkanz.pay.controller.BaseController;
import com.lxn.lkanz.pay.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: lxn
 * @Date: 2019/12/25 14:14
 * @Version 1.0
 */
@Slf4j
@Controller
public class TestController extends BaseController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //    public String payHost = "http://192.168.10.179:3020";
    public String payHost = "http://218.27.78.18:13020";
    /**
     * XxPay系统分配商户Id
     */
    private String mchId = "20000003";
    /**
     * 商户秘钥
     */
    private String appKey = "8RAKBPBN7ZSONY4L3QGPYBB5OAGHHYCTCZ40NV9U9ES3VOVNOKCWQ6X59QK7NLBPLS8SG8FAQA4HXHTMKZHTA0MPLG5XVVTMLSQNSK6L73EQQDYDYW2RUBLHSUJBOQA4";

    /**
     * 格润物联公众号支付
     *
     * @return
     */
    @RequestMapping(value = "/test/wxjsapi")
    public String greenJSApi(HttpServletRequest request, Model model) {
        String openid = request.getParameter("openid");
        log.info("格润物联公众号openid = {}", openid);
        JSONObject jsonObject = JSON.parseObject(getUnifiedOrderParams("JSAPI", openid).toString());
        JSONObject payParams = JSON.parseObject(jsonObject.get("payParams").toString());
        getWxPayModel(model, payParams);
        return "pay";
    }

    /**
     * 健康长春公众号支付
     *
     * @return
     */
    @RequestMapping(value = "/test/jkcc/wxjsapi")
    public String JkccJSApi(HttpServletRequest request, Model model) {
        String openid = request.getParameter("openid");
        log.info("健康长春公众号openid = {}", openid);
        JSONObject jsonObject = JSON.parseObject(getJkccUnifiedOrderParams(openid).toString());
        JSONObject payParams = JSON.parseObject(jsonObject.get("payParams").toString());
        getWxPayModel(model, payParams);
        return "pay";
    }

    /**
     * xxpay支付
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/test/xxpayjsapi")
    public Object getXXPayParamsForJSAPI(HttpServletRequest request, Model model) throws Exception {
        String url = payHost + "/api/pay/create_order";
        Map<String, String> params = getXXUnifiedOrderParams(mchId, appKey);
        log.info("XxPay统一下单请求参数：" + JSONObject.toJSONString(params));
        String result = HttpUtil.doPostSSL(url, JSONObject.toJSONString(params), "application/json");
        log.info("XxPay统一下单响应参数：" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = JSONObject.parseObject(jsonObject.get("data").toString());
        JSONObject payParams = data.getJSONObject("payParams");
        log.info("XxPay统一下单响应支付参数：" + payParams.toJSONString());
        getWxPayModel(model, payParams);
        log.info("返回前端支付参数：" + payParams.toJSONString());
        return "pay";
    }

    /**
     * 公众号地址链接获取用户code，通过code获取用户openid(格润物联公众号)
     *
     * @return
     */
    @RequestMapping(value = "/wx/code")
    public String wxIndex(HttpServletRequest request, Model model) {
        //获取用户的code
        String code = request.getParameter("code");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx17264fb6093e19c7&secret=db43164cf686f111180600b588183e8d&code=" + code + "&grant_type=authorization_code";
        String msg = "格润物联公众号";
        log.info("code = {}", code);
        String openid = getOpenid(url, msg);
        model.addAttribute("openid", openid);
        return "index";
    }

    /**
     * 公众号地址链接获取用户code，通过code获取用户openid(健康城市长春公众号)
     *
     * @return
     */
    @RequestMapping(value = "/wx/jkcc/code")
    public String wxIndexByJkcc(HttpServletRequest request, Model model) {
        //获取用户的code
        String code = request.getParameter("code");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx56a784a2802804b7&secret=c5646c6fc66745f256ea96dd71c12b5b&code=" + code + "&grant_type=authorization_code";
        String msg = "健康长春公众号";
        log.info("{}code = {}", msg, code);
        String openid = getOpenid(url, msg);
        model.addAttribute("jkccOpenid", openid);
        return "index";
    }

    /**
     * 获取用户openid
     *
     * @param url 网页授权地址
     * @param msg 来源渠道标识
     * @return
     */
    public String getOpenid(String url, String msg) {
        String jsonResult = HttpUtil.doGet(url);
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        log.info("{}获取openid返回结果：{}", msg, jsonResult);
        String openid = jsonObject.get("openid").toString();
        log.info("健康长春公众号openid = {}", openid);
        return openid;
    }

    /**
     * 组装返回前端页面参数
     *
     * @param model
     * @param payParams
     * @return
     */
    private Model getWxPayModel(Model model, JSONObject payParams) {
        model.addAttribute("appId", payParams.get("appId"));
        model.addAttribute("timeStamp", payParams.get("timeStamp"));
        model.addAttribute("nonceStr", payParams.get("nonceStr"));
        model.addAttribute("package", payParams.get("package"));
        model.addAttribute("signType", payParams.get("signType"));
        model.addAttribute("paySign", payParams.get("sign"));
        return model;
    }

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
}
