package com.lxn.lkanz.pay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxn.lkanz.pay.util.HttpUtil;
import com.lxn.lkanz.pay.util.WXPayConstants;
import com.lxn.lkanz.pay.util.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author: lxn
 * @Date: 2019/12/19 17:23
 * @Version 1.0
 */
@Slf4j
@RestController
public class WxPayController extends BaseController{

    /**
     * 微信验证服务器
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/lxn/api/getToken", method = {RequestMethod.GET}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getWxToken(HttpServletRequest request) {
        return "hello2019";
    }

    /**
     * 微信官方接口：统一下单
     *
     * @return
     */
    @RequestMapping(value = "/api/pay/unifiedOrder", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String unifiedOrder(HttpServletRequest request) {
        try {
            //1.统一下单
            String urlString = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            SortedMap<String, String> packageParams = new TreeMap<String, String>();
            JSONObject jsonObject = JSONObject.parseObject(HttpUtil.ReadAsChars(request));
            String apiKey = jsonObject.getString("key");
            String mchId = jsonObject.getString("mchId");
            String appId = jsonObject.getString("appId");
            log.info("接收移动端参数：" + jsonObject.toJSONString());
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
            packageParams.put("out_trade_no", "lxn_150_" + WXPayUtil.getCurrentTimestampMs());
            //总金额
            packageParams.put("total_fee", "1");
            //交易类型
            packageParams.put("trade_type", "APP");
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
                //获取支付参数
                payParams = getPayParams(resultXml, apiKey);
            } else {
                payParams = JSONObject.toJSONString(WXPayUtil.xmlToMap(resultXml));
            }
            JSONObject resultJSON = new JSONObject();
            resultJSON.put("wxPayOrderParams", JSONObject.parseObject(wxPayOrderParams));
            resultJSON.put("payParams", JSONObject.parseObject(payParams));
            log.info("返回前端支付参数：\n{}", resultJSON.toJSONString());
            return resultJSON.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    /**
     * 微信官方接口：退款
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/pay/refund", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String refundFromWX(HttpServletRequest request) throws Exception {
        String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";
        Map<String, String> map = new HashMap<String, String>();
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.ReadAsChars(request));
        String mchId = jsonObject.getString("mchId");
        String appId = jsonObject.getString("appId");
        String apiKey = jsonObject.getString("key");
        String certName = jsonObject.getString("certLocalPath");
        String outTradeNo = jsonObject.getString("outTradeNo");
        //APP_ID
        map.put("appid", appId);
        //商户号
        map.put("mch_id", mchId);
        //随机字符串
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        //签名类型
        map.put("sign_type", "MD5");
        //微信订单号(与商户订单号二选一)
        map.put("transaction_id", "");
        //商户订单号(与微信订单号二选一)
        map.put("out_trade_no", outTradeNo);
        //商户退款单号
        map.put("out_refund_no", WXPayUtil.getCurrentTimestampMs() + "");
        //订单金额
        map.put("total_fee", "1");
        //退款金额
        map.put("refund_fee", "1");
        //退款货币种类
        map.put("refund_fee_type", "CNY");
        //退款原因
        map.put("refund_desc", "商品已售完");
        //退款资金来源
        map.put("refund_account", "");
        //退款结果通知url
        map.put("notify_url", "http://123.56.87.185/api/pay/callBackFromWx");
        //生成带有sign的xml字符串
        String generateSignedXml = WXPayUtil.generateSignedXml(map, apiKey);
        //访问微信接口
        String certPath = WXPayConstants.certPath + "/" + certName;
        String resultXml = HttpUtil.doPostSSL(url, generateSignedXml, mchId, certPath);
        log.info("退款返回结果: \n{}", resultXml);
        return JSONObject.toJSONString(WXPayUtil.xmlToMap(resultXml));
    }

    /**
     * 微信官方接口：查询订单
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/api/pay/query", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String queryWxOrder(HttpServletRequest request) throws Exception {
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        Map<String, String> map = new HashMap<String, String>();
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.ReadAsChars(request));
        String mchId = jsonObject.getString("mchId");
        String appId = jsonObject.getString("appId");
        String apiKey = jsonObject.getString("key");
        String outTradeNo = jsonObject.getString("outTradeNo");
        //微信开放平台审核通过的应用APPID
        map.put("appid", appId);
        //微信支付分配的商户号
        map.put("mch_id", mchId);
        //商户系统内部的订单号，当没提供transaction_id时需要传这个
        map.put("out_trade_no", outTradeNo);
        //随机字符串，不长于32位。推荐随机数生成算法
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        String sign = WXPayUtil.generateSignature(map, apiKey);
        map.put("sign", sign);
        String xmlString = WXPayUtil.mapToXml(map);
        log.info("微信订单查询请求参数：\n{}", xmlString);
        String resultXml = HttpUtil.doPostSSL(url, xmlString);
        log.info("微信订单查询返回结果：\n{}", resultXml);
        return JSON.toJSONString(WXPayUtil.xmlToMap(resultXml));
    }

    /**
     * 查询退款订单
     *
     * @throws Exception
     */
    @RequestMapping(value = "/api/pay/queryRefund", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String queryRefund(HttpServletRequest request) throws Exception {
        String url = "https://api.mch.weixin.qq.com/pay/refundquery";
        Map<String, String> map = new HashMap<String, String>();
        JSONObject jsonObject = JSONObject.parseObject(HttpUtil.ReadAsChars(request));
        String mchId = jsonObject.getString("mchId");
        String appId = jsonObject.getString("appId");
        String apiKey = jsonObject.getString("key");
        String outTradeNo = jsonObject.getString("outTradeNo");
        //微信开放平台审核通过的应用APPID
        map.put("appid", appId);
        //微信支付分配的商户号
        map.put("mch_id", mchId);
        //随机字符串，不长于32位。推荐随机数生成算法
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        //微信的订单号，优先使用
        map.put("out_trade_no", outTradeNo);
        String queryRefundXml = WXPayUtil.generateSignedXml(map, apiKey);
        log.info("退款查询请求参数：\n{}", queryRefundXml);
        String resultXml = HttpUtil.doPostSSL(url, queryRefundXml);
        log.info("退款查询返回结果：\n{}", resultXml);
        return JSON.toJSONString(WXPayUtil.xmlToMap(resultXml));
    }

    /**
     * 微信支付回调地址
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/lxn/api/callBackFromWx", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String lxnCallBackFromWx(HttpServletRequest request) {
        return callBackFromWx(request);
    }
    /**
     * 微信支付回调地址
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/pay/callBackFromWx", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String callBackFromWx(HttpServletRequest request) {
        try {
            String resultFromWx = HttpUtil.ReadAsChars(request);
            //重新排版格式，多行显示
            String resultFormat = WXPayUtil.mapToXml(WXPayUtil.xmlToMap(resultFromWx));
            log.info("微信支付回调结果：\n{}", resultFormat);
            Map<String, String> resultToWx = new HashMap<>(2);
            resultToWx.put("return_code", "SUCCESS");
            resultToWx.put("return_msg", "OK");
            return WXPayUtil.mapToXml(resultToWx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/api/pay/upload", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String saveFile(@RequestParam(value = "file") MultipartFile file) {
        String result = "";
        try {
            file.transferTo(new File(WXPayConstants.certPath, file.getOriginalFilename()));
            result = file.getOriginalFilename() + "上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            result = file.getOriginalFilename() + "上传失败";
        }
        log.info("上传证书结果：{}", result);
        return result;
    }

}
