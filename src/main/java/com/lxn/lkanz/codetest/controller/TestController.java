package com.lxn.lkanz.codetest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxn.lkanz.entity.User;
import com.lxn.lkanz.pay.controller.BaseController;
import com.lxn.lkanz.pay.util.HttpUtil;
import com.lxn.lkanz.pay.util.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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

    public String payHost = "http://192.168.10.179:3020";
    /**
     * XxPay系统分配商户Id
     */
    private String mchId = "20000003";
    /**
     * 商户秘钥
     */
    private String appKey = "8RAKBPBN7ZSONY4L3QGPYBB5OAGHHYCTCZ40NV9U9ES3VOVNOKCWQ6X59QK7NLBPLS8SG8FAQA4HXHTMKZHTA0MPLG5XVVTMLSQNSK6L73EQQDYDYW2RUBLHSUJBOQA4";

    /**
     * @return
     */
    @RequestMapping(value = "/test/wxjsapi")
    public String test(HttpServletRequest request, Model model) {
        String openid = request.getParameter("openid");
        log.info("openid = {}",openid);
        JSONObject jsonObject = JSON.parseObject(getUnifiedOrderParams("JSAPI",openid).toString());
        JSONObject payParams = JSON.parseObject(jsonObject.get("payParams").toString());
        model.addAttribute("appId", payParams.get("appId"));
        model.addAttribute("timeStamp", payParams.get("timeStamp"));
        model.addAttribute("nonceStr", payParams.get("nonceStr"));
        model.addAttribute("package", payParams.get("package"));
        model.addAttribute("signType", payParams.get("signType"));
        model.addAttribute("paySign", payParams.get("paySign"));
        return "pay";
    }

    /**
     * @return
     */
    @RequestMapping(value = "/wx/code")
    public String index(HttpServletRequest request, Model model) {
        //获取用户的code
        String code = request.getParameter("code");
        log.info("code = {}", code);
        String openid = getOpenid(code);
        log.info("openid = {}", openid);
        model.addAttribute("openid", openid);
        return "index";
    }

    /**
     * 获取openid
     *
     * @param code
     * @return
     */
    public String getOpenid(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx17264fb6093e19c7&secret=db43164cf686f111180600b588183e8d&code=" + code + "&grant_type=authorization_code";
        String jsonResult = doGet(url);
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        log.info("jsonResult = {}", jsonResult);
        String openid = jsonObject.get("openid").toString();
        return openid;
    }

    /**
     * http get 请求
     *
     * @param url url
     */
    public static String doGet(String url) {
        if (url == null || url.isEmpty()) {
            return "url is null";
        }
        String respCtn = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpResponse response = null;
            HttpGet get = new HttpGet(url);
            response = httpclient.execute(get);
            respCtn = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                }
            }
        }
        return respCtn;
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

    @ResponseBody
    @RequestMapping(value = "/batchRefund")
    public String batchRefund() throws Exception {
//        mchId = "20000000";
//        appKey = "D4SZ8TQK1Z8UPYMOLSKQQPMWYKVXW8IAHBMNJEFXJLCYPF7AWKCTKN1SXWS82ZNPMOBRFEGCK5TOGOQKPC59LP0FHIP6TU5GZ5TZXHHJ7YDGHSWP2URHZX1YUKPUMPAM";
        String sql = "SELECT PayOrderId FROM t_pay_order WHERE Status=3 AND MchId=20000003";
        List<String> list = jdbcTemplate.queryForList(sql, String.class);
        for (String payOrderId : list) {
            log.info("payOrderId = {}", payOrderId);
            refund(mchId, appKey, payOrderId);
        }
        return "success";
    }

    @RequestMapping(value = "/callBackUrl")
    @ResponseBody
    public String callBackUrl(HttpServletRequest request) {
        String url = request.getParameter("url");
        String result = "";
        try {
            result = HttpUtil.doPostSSL(url, "");
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        log.info("result = ", result);
        return result;
    }

    /**
     * 退款订单
     *
     * @param mchId  XxPay商户ID
     * @param appKey XxPay商户秘钥
     * @return
     * @throws Exception
     */
    public String refund(String mchId, String appKey, String outTradeNo) throws Exception {
        String url = payHost + "/api/refund/create_order";
        Map<String, Object> params = getRefundParams(mchId, appKey, outTradeNo);
        System.out.println("XxPay退款订单请求参数：" + JSON.toJSONString(params));
        String result = HttpUtil.doPostSSL(url, JSONObject.toJSONString(params), null, null, false, "application/json");
        System.out.println("XxPay退款订单响应参数：" + result);
        return JSONObject.toJSONString(params);
    }

    public Map<String, Object> getRefundParams(String mchId, String appKey, String outTradeNo) throws Exception {
        Map<String, Object> params = new HashMap();
        // 商户ID
        params.put("mchId", mchId);
        // 应用ID
        params.put("appId", "");
        // 支付订单号
        params.put("payOrderId", outTradeNo);
        // 商户支付单号
        params.put("mchOrderNo", "");
        // 商户退款单号
        params.put("mchRefundNo", "lxn_refund_" + WXPayUtil.getCurrentTimestampMs());
        // 退款金额（单位分）
        params.put("amount", "1");
        // 退款结果回调URL,如果填写则退款结果会通过该url通知
        params.put("notifyUrl", "http://123.56.87.185/api/pay/notify");
        String sign = WXPayUtil.getSign(params, appKey);
        params.put("sign", sign);
        return params;
    }
}
