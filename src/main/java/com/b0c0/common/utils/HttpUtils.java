package com.b0c0.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.b0c0.common.InteriorThreadPoolFactory;
import com.b0c0.common.domain.vo.GeneralResultVo;
import com.b0c0.common.log.GeneralPrintLogAOP;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * HttpClientUtils用来发送HTTP请求
 */
public class HttpUtils {

    private static final Logger logger = Logger.getLogger(HttpUtils.class.getName());

    private static CloseableHttpClient httpClient;

    private static PoolingHttpClientConnectionManager cm;

    private static final RequestConfig requestConfig = RequestConfig.custom()
            // 套接字超时（SO_TIMEOUT）以毫秒为单位
            .setSocketTimeout(5000)
            // 建立连接之前的超时时间（以毫秒为单位）。
            .setConnectTimeout(5000)
            // 从连接池请求连接时使用的超时（以毫秒为单位）。
            .setConnectionRequestTimeout(5000)
            .build();

    static {
        init();
        closeExpiredConnectionsPeriodTask(60);
    }

    static void init() {
        cm = new PoolingHttpClientConnectionManager();
        // 最大连接数
        cm.setMaxTotal(20);
        // 每条路线的最大连接数
        cm.setDefaultMaxPerRoute(2);
        // 设置指定路由的最大连接数
        cm.setMaxPerRoute(new HttpRoute(new HttpHost("locahost", 80)), 50);
        httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
    }

    /**
     * 根据 PoolingHttpClientConnectionManager配置 重新设置 httpClient
     *
     * @param cm
     */
    public static void setHttpClient(PoolingHttpClientConnectionManager cm, RequestConfig requestConfig) {
        closeExpiredConnectionsPeriodTask(60);
        httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
    }

    public static CloseableHttpClient getNewHttpClient(PoolingHttpClientConnectionManager cm, RequestConfig requestConfig) {
        return HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
    }

    private static void closeExpiredConnectionsPeriodTask(int timeUnitBySecond) {
        InteriorThreadPoolFactory.getGeneral().execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(timeUnitBySecond);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cm.closeExpiredConnections();
            }
        });
    }

    public enum HttpMethod {
        GET,
        POST
    }

    public enum MediaType {
        //json 字符串格式数据
        JSON("application/json;charset=UTF-8"),
        //from 表单格式数据
        FROM("application/x-www-form-urlencoded");
        private String code;

        MediaType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * 统一请求
     *
     * @param url        请求地址
     * @param headParams 请求头参数
     * @param bodyParams 请求体参数
     * @param httpMethod http请求方式
     * @param mediaType  请求数据类型
     * @return
     */
    @GeneralPrintLogAOP(value = "统一请求")
    public GeneralResultVo<String> reqHolder(String url, String headParams, String bodyParams, HttpMethod httpMethod, MediaType mediaType) {
        try {
            Map<String, String> map = JSONObject.parseObject(bodyParams, HashMap.class);
            String resultString = null;
            if (httpMethod == HttpMethod.GET) {
                resultString = doGet(httpClient, url, headParams, map);
            } else {
                if (mediaType == MediaType.FROM) {
                    resultString = doPost(httpClient, url, headParams, map);
                } else if (mediaType == MediaType.JSON) {
                    resultString = doPostJson(httpClient, url, headParams, bodyParams);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return GeneralResultVo.fail();
        }
        return GeneralResultVo.fail();
    }

    /**
     * 统一请求
     *
     * @param url        请求地址
     * @param headParams 请求头参数
     * @param bodyParams 请求体参数
     * @param httpMethod http请求方式
     * @param mediaType  请求数据类型
     * @return
     */
    @GeneralPrintLogAOP(value = "统一请求")
    public GeneralResultVo<String> reqHolder(CloseableHttpClient httpClient, String url, String headParams, String bodyParams, HttpMethod httpMethod, MediaType mediaType) {
        try {
            Map<String, String> map = JSONObject.parseObject(bodyParams, HashMap.class);
            String resultString = null;
            if (httpMethod == HttpMethod.GET) {
                resultString = doGet(httpClient, url, headParams, map);
            } else {
                if (mediaType == MediaType.FROM) {
                    resultString = doPost(httpClient, url, headParams, map);
                } else if (mediaType == MediaType.JSON) {
                    resultString = doPostJson(httpClient, url, headParams, bodyParams);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return GeneralResultVo.fail();
        }
        return GeneralResultVo.fail();
    }

    private static String execute(CloseableHttpClient httpClient, HttpUriRequest httpUriRequest, String headParams) {
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            if (headParams != null && !headParams.isEmpty()) {
                JSONObject jsonObject = JSONObject.parseObject(headParams);
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    httpUriRequest.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            // 执行请求
            response = httpClient.execute(httpUriRequest);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), getDefaultCharSet());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * 发送get请求
     *
     * @param url   请求地址
     * @param param 参数
     * @return
     */
    public static String doGet(CloseableHttpClient httpClient, String url, String headParams, Map<String, String> param) throws URISyntaxException {
        // 创建uri
        URIBuilder builder = new URIBuilder(url);
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addParameter(key, param.get(key));
            }
        }
        URI uri = builder.build();
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(uri);
        execute(httpClient, httpGet, headParams);
        return null;
    }

    /**
     * 发送get请求，不带参数
     *
     * @param url 请求地址
     * @return
     */
    public static String doGet(CloseableHttpClient httpClient, String url) throws URISyntaxException {
        return doGet(httpClient, url, null, null);
    }

    /**
     * 发送POST请求，携带map格式的参数
     * 如：name="jok",age="10"
     *
     * @param url   请求地址
     * @param param Map格式的参数
     * @return
     */
    public static String doPost(CloseableHttpClient httpClient, String url, String headParams, Map<String, String> param) throws UnsupportedEncodingException {
        // 创建Http Post请求
        HttpPost httpPost = new HttpPost(url);
        // 创建参数列表
        if (param != null) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (String key : param.keySet()) {
                paramList.add(new BasicNameValuePair(key, param.get(key)));
            }
            // 模拟表单
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, getDefaultCharSet());
            httpPost.setEntity(entity);
        }
        return execute(httpClient, httpPost, headParams);
    }

    /**
     * 发送POST请求，不带参数
     *
     * @param url 请求地址
     * @return
     */
    public static String doPost(CloseableHttpClient httpClient, String url) throws UnsupportedEncodingException {
        return doPost(httpClient, url, null, null);
    }

    /**
     * 发送post请求，携带json类型数据
     * 如：{"name":"jok","age":"10"}
     *
     * @param url  请求地址
     * @param json json格式参数
     * @return
     */
    public static String doPostJson(CloseableHttpClient httpClient, String url, String headParams, String json) {
        // 创建Http Post请求
        HttpPost httpPost = new HttpPost(url);
        // 创建请求内容
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        return execute(httpClient, httpPost, headParams);
    }

    /**
     * 设置编码格式utf-8,防止乱码
     *
     * @return utf-8
     */
    private static String getDefaultCharSet() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
        String enc = writer.getEncoding();
        return enc;
    }

}