package com.lxn.lkanz.pay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxn.lkanz.pay.util.HttpUtil;
import com.lxn.lkanz.pay.util.WXPayConstants;
import com.lxn.lkanz.pay.util.WXPayUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author: lxn
 * @Date: 2020/1/21 16:29
 * @Version 1.0
 */
@Slf4j
public class BaseController {

    protected Object getUnifiedOrderParams(String tradeType) {
        return getUnifiedOrderParams(tradeType,null);
    }
    /**
     * 获取下单参数
     *
     * @return
     */
    protected Object getUnifiedOrderParams(String tradeType,String openid) {
        try {
            //1.统一下单
            String urlString = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            SortedMap<String, String> packageParams = new TreeMap<String, String>();
            String apiKey = WXPayConstants.MCH_KEY;
            String appId = "";
            if("APP".equals(tradeType)){
                appId = WXPayConstants.APP_ID;
            }else if("JSAPI".equals(tradeType)){
                appId = WXPayConstants.JSAPI_ID;
            }
            String mchId = WXPayConstants.MCH_ID;
            //应用ID
            packageParams.put("appid", appId);
            //商品描述
            packageParams.put("body", "市民电子健康卡");
            //商户号
            packageParams.put("mch_id", mchId);
            //随机字符串
            packageParams.put("nonce_str", WXPayUtil.generateNonceStr());
            // 回调地址
            packageParams.put("notify_url", "http://123.56.87.185/lxn/api/callBackFromWx");
            //商户订单号
            packageParams.put("out_trade_no", "lxn_185_" + WXPayUtil.getCurrentTimestampMs());
            //总金额
            packageParams.put("total_fee", "1");
            //交易类型
            packageParams.put("trade_type", tradeType);
            packageParams.put("openid",openid);
            String sign = WXPayUtil.generateSignature(packageParams, apiKey);
            packageParams.put("sign", sign);
            String xmlString = WXPayUtil.mapToXml(packageParams);
            log.info("统一下单请求参数：\n{}", xmlString);
            String resultXml = HttpUtil.doPostSSL(urlString, xmlString);
            log.info("统一下单微信返回结果：\n{}", resultXml);
            Map<String, String> mapResult = WXPayUtil.xmlToMap(resultXml);
            //请求参数
            String wxPayOrderParams = JSONObject.toJSONString(packageParams);
            String payParams = "";
            if ("SUCCESS".equals(mapResult.get("return_code"))) {
                switch (tradeType) {
                    case "APP":
                        //获取APP支付参数
                        payParams = getPayParams(resultXml, apiKey);
                        break;
                    case "JSAPI":
                        //获取公众号支付参数
                        payParams = getMpPayParams(resultXml,apiKey);
                        break;
                    default:
                        break;
                }
            } else {
                payParams = JSONObject.toJSONString(WXPayUtil.xmlToMap(resultXml));
            }
            JSONObject resultJSON = new JSONObject();
            resultJSON.put("wxPayOrderParams", JSONObject.parseObject(wxPayOrderParams));
            resultJSON.put("payParams", JSONObject.parseObject(payParams));
            log.info("返回前端支付参数：\n{}", resultJSON.toJSONString());
            return resultJSON;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 微信官方接口：调起APP支付接口
     *
     * @param resultXml
     * @throws Exception
     */
    public String getPayParams(String resultXml, String apiKey) throws Exception {
        Map<String, String> map = WXPayUtil.xmlToMap(resultXml);
        //2.生成签名参数
        SortedMap<String, String> params = new TreeMap<String, String>();
        //应用ID
        params.put("appid", map.get("appid"));
        //随机字符串
        params.put("noncestr", WXPayUtil.generateNonceStr());
        //扩展字段
        params.put("package", "Sign=WXPay");
        //商户号
        params.put("partnerid", map.get("mch_id"));
        //预支付交易会话ID
        params.put("prepayid", map.get("prepay_id"));
        //时间戳
        params.put("timestamp", WXPayUtil.getCurrentTimestamp() + "");
        String sign2 = WXPayUtil.generateSignature(params, apiKey);
        //签名
        params.put("sign", sign2);
        return JSON.toJSONString(params);
    }

    /**
     * 微信公众号支付参数
     *
     * @param resultXml
     * @throws Exception
     */
    public String getMpPayParams(String resultXml, String apiKey) throws Exception {
        Map<String, String> map = WXPayUtil.xmlToMap(resultXml);
        //2.生成签名参数
        SortedMap<String, String> params = new TreeMap<String, String>();
        //应用ID
        params.put("appId", map.get("appid"));
        //时间戳
        params.put("timeStamp", WXPayUtil.getCurrentTimestamp() + "");
        //随机字符串
        params.put("nonceStr", WXPayUtil.generateNonceStr());
        //扩展字段
        params.put("package", "prepay_id="+map.get("prepay_id"));
        //商户号
        params.put("signType", "MD5");
        String sign2 = WXPayUtil.generateSignature(params, apiKey);
        //签名
        params.put("paySign", sign2);
        return JSON.toJSONString(params);
    }
}
