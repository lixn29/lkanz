package com.lxn.lkanz.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.lxn.lkanz.pay.util.HttpUtil;
import com.lxn.lkanz.pay.util.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lxn
 * @Date: 2019/12/19 17:25
 * @Version 1.0
 */
@Slf4j
@RestController
public class WxPayNotifyController {

    @Value("${config.mchId}")
    private Long mchId;

    @Value("${config.mchKey}")
    private String mchKey;

    @Value("${config.payHost}")
    private String payHost;

    /**
     * xxpay商业版通知
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/api/pay/notify")
    @ResponseBody
    public String notify(HttpServletRequest request) throws Exception {
        //接口返回sign参数值
        String resSign = request.getParameter("sign");
        Map<String, Object> paramsMap = new HashMap();
        paramsMap.put("payOrderId", request.getParameter("payOrderId"));
        paramsMap.put("mchId", request.getParameter("mchId"));
        paramsMap.put("appId", request.getParameter("appId"));
        paramsMap.put("productId", request.getParameter("productId"));
        paramsMap.put("mchOrderNo", request.getParameter("mchOrderNo"));
        paramsMap.put("amount", request.getParameter("amount"));
        paramsMap.put("status", request.getParameter("status"));
        paramsMap.put("channelOrderNo", request.getParameter("channelOrderNo"));
        paramsMap.put("channelAttach", request.getParameter("channelAttach"));
        paramsMap.put("param1", request.getParameter("param1"));
        paramsMap.put("param2", request.getParameter("param2"));
        paramsMap.put("paySuccTime", request.getParameter("paySuccTime"));
        paramsMap.put("backType", request.getParameter("backType"));
        paramsMap.put("income", request.getParameter("income"));
        paramsMap.put("payPassAccountId", request.getParameter("payPassAccountId"));
        paramsMap.put("currency", request.getParameter("currency"));
        paramsMap.put("device", request.getParameter("device"));
        paramsMap.put("refundOrderId", request.getParameter("refundOrderId"));
        paramsMap.put("mchRefundNo", request.getParameter("mchRefundNo"));
        paramsMap.put("refundAmount", request.getParameter("refundAmount"));
        paramsMap.put("refundStatus", request.getParameter("refundStatus"));
        paramsMap.put("refundSuccTime", request.getParameter("refundSuccTime"));
        paramsMap.put("result", request.getParameter("result"));

        log.info("平台支付信息通知（notify）：{}", JSONObject.toJSONString(paramsMap));
        //根据返回数据 和商户key 生成sign
        String sign = WXPayUtil.getSign(paramsMap, mchKey);
        //验签
        if (!resSign.equals(sign)) {
            log.info("验签失败：sign={},localSign={}", resSign, sign);
            return "validate sign fail";
        } else {
            log.info("验签成功：sign = {}", sign);
            return "success";
        }
    }

    @RequestMapping(value = "/api/pay/notify2")
    @ResponseBody
    public String notifyByLxn(HttpServletRequest request) throws IOException {
        try {
            Map<String, String[]> resultFromWx = request.getParameterMap();
            log.info("平台异步通知参数（notify2）：{}", JSONObject.toJSONString(resultFromWx));
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @RequestMapping(value = "/api/pay/notify3")
    @ResponseBody
    public String notifyForWx(HttpServletRequest request) throws Exception {
        try {
            String resultFromWx = HttpUtil.ReadAsChars(request);
            log.info("平台异步通知参数（notify3）：{}", resultFromWx);
            log.info("平台异步通知参数（notify3）：{}", JSONObject.toJSONString(resultFromWx));
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        Map<String,String> result = new HashMap<>();
        result.put("return_code","SUCCESS");
        result.put("return_msg","OK");
        return WXPayUtil.mapToXml(result);
    }
}
