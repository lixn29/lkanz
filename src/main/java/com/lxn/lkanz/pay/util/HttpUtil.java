package com.lxn.lkanz.pay.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import static com.lxn.lkanz.pay.util.WXPayConstants.USER_AGENT;

/**
 * @Author: lxn
 * @Date: 2019/12/23 14:35
 * @Version 1.0
 */
public class HttpUtil {
    // 字符串读取
    // 方法一
    public static String ReadAsChars(HttpServletRequest request) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder("");
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 不需要证书，content_type="text/xml"
     *
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static String doPostSSL(String url, String data) throws Exception {
        return doPostSSL(url, data, null, null, false, "text/xml");
    }

    /**
     * 需要证书,content_type="text/xml"
     *
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static String doPostSSL(String url, String data, String mchId, String certPath) throws Exception {
        return doPostSSL(url, data, mchId, certPath, true, "text/xml");
    }
    /**
     * 不需要证书,content_type自选
     *
     * @param url
     * @param data
     * @param content_type
     * @return
     * @throws Exception
     */
    public static String doPostSSL(String url, String data, String content_type) throws Exception {
        return doPostSSL(url, data, null, null, false, content_type);
    }

    /**
     * http访问
     *
     * @param url          地址
     * @param data         数据
     * @param useCert      是否需要证书
     * @param content_type 头文件类型
     * @return 响应内容
     * @throws IOException
     */
    public static String doPostSSL(String url, String data, String mchId, String certPath, boolean useCert, String content_type) throws Exception {
        BasicHttpClientConnectionManager connManager;
        if (useCert) {
            byte[] certData;
            File file = new File(certPath);
            InputStream certStream1 = new FileInputStream(file);
            certData = new byte[(int) file.length()];
            certStream1.read(certData);
            certStream1.close();

            ByteArrayInputStream certBis = new ByteArrayInputStream(certData);
            // 证书
            char[] password = mchId.toCharArray();
            InputStream certStream2 = certBis;
            ;
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certStream2, password);

            // 实例化密钥库 & 初始化密钥工厂
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);

            // 创建 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1"},
                    null,
                    new DefaultHostnameVerifier());

            connManager = new BasicHttpClientConnectionManager(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", sslConnectionSocketFactory)
                            .build(),
                    null,
                    null,
                    null
            );
        } else {
            connManager = new BasicHttpClientConnectionManager(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", SSLConnectionSocketFactory.getSocketFactory())
                            .build(),
                    null,
                    null,
                    null
            );
        }
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();
        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(8000).setConnectTimeout(6000).build();
        httpPost.setConfig(requestConfig);

        StringEntity postEntity = new StringEntity(data, "UTF-8");
        httpPost.addHeader("Content-Type", content_type);
        httpPost.addHeader("User-Agent", USER_AGENT + " " + "103854678");
        httpPost.setEntity(postEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");
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

    public static void main(String[] args) throws Exception {
        String respUrl = "http://172.16.9.50:8888/v1/pay/payNotify?income=900&payOrderId=P01202001071503208730009&amount=900&mchId=20000004&productId=8019&mchOrderNo=1578380513007000001400&paySuccTime=2020-01-07+15%3A03%3A45&sign=353501F6868E7B09E42F68699FE83F47&channelOrderNo=4200000505202001077911113399&backType=2&param1=&param2=&status=2&payPassAccountId=8";
//      respUrl = "http://192.168.10.179/api/pay/notify3";
//        respUrl = "http://192.168.10.179:3020/api/pay/notify3";
        String data = "";
        String result = HttpUtil.doPostSSL(respUrl, data, null, null, false, "application/json");
        System.out.println("result = " + result);
    }
}
