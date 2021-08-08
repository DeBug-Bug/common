package com.b0c0.common.utils;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSONObject;
import com.b0c0.common.domain.vo.GeneralResultVo;
import com.b0c0.common.factory.InteriorThreadPoolFactory;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

/**
 * HttpClientUtils用来发送HTTP请求
 */
public class HttpXHelper {

    private static final Logger logger = Logger.getLogger(HttpXHelper.class.getName());

    private CloseableHttpClient httpClient;

    private PoolingHttpClientConnectionManager cm;

    private HttpHost proxy;

    /**
     * true 开启重试  false 关闭重试
     */
    private boolean openRetry = false;

    /**
     * 重试策略
     */
    private HttpRequestRetryHandler retryHandler = creatHttpRequestRetryHandler();

    /**
     * 重试次数
     */
    private Integer retryNum = 3;

    /**
     * 重试间隔 ms 毫秒
     */
    private Long retryInterval = 0L;

    private ServiceUnavailableRetryStrategy serviceUnavailStrategy = new ServiceUnavailableRetryStrategy() {
        /**
         * retry逻辑
         */
        @Override
        public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
            logger.info("retryRequest次数为:" + executionCount);
            //当返回状态码不为200（成功）的情况下重试，重试次数默认设为3次
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK && executionCount <= retryNum) {
                return true;
            }
            return false;
        }

        /**
         * retry间隔时间
         */
        @Override
        public long getRetryInterval() {
            return retryInterval;
        }
    };
    /**
     * 保活连接
     */
    private ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
        HeaderElementIterator it = new BasicHeaderElementIterator
                (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase
                    ("timeout")) {
                return Long.parseLong(value) * 1000;
            }
        }
        return 5 * 1000;//如果没有约定，则默认定义时长为5s
    };

    private RequestConfig requestConfig = RequestConfig.custom()
            // 套接字超时（SO_TIMEOUT）以毫秒为单位
            .setSocketTimeout(5000)
            // 建立连接之前的超时时间（以毫秒为单位）。
            .setConnectTimeout(5000)
            // 从链接池获取连接超时时间（以毫秒为单位）。
            .setConnectionRequestTimeout(5000)
            .build();

    private HttpRequestRetryHandler creatHttpRequestRetryHandler() {
        return (e, i, httpContext) -> {
            HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) httpContext.getAttribute("http.request");
            if (i > 2) {
                logger.severe("【连接重试】 超过2次 放弃请求:" + httpRequestWrapper.getOriginal().toString());
                return false;
            }
            logger.severe("【连接重试】 第 " + i + " 次重试请求 -> :" + httpRequestWrapper.getOriginal().toString());
            if (e instanceof NoHttpResponseException) {
                //服务器没有响应,可能是服务器断开了连接,应该重试
                logger.severe("【连接重试】 服务器没有响应 重试:" + httpRequestWrapper.getOriginal().toString());
                return true;
            }
            if (e instanceof SSLHandshakeException) {
                // SSL握手异常
                logger.severe("【连接重试】 SSL握手异常 重试:" + httpRequestWrapper.getOriginal().toString());
                return false;
            }
            if (e instanceof InterruptedIOException) {
                //超时
                logger.severe("【连接重试】 超时 重试:" + httpRequestWrapper.getOriginal().toString());
                return true;
            }
            if (e instanceof UnknownHostException) {
                // 服务器不可达
                logger.severe("【连接重试】 服务器不可达 不重试:" + httpRequestWrapper.getOriginal().toString());
                return false;
            }
            if (e instanceof ConnectTimeoutException) {
                // 连接超时
                logger.severe("【连接重试】 连接超时 重试:" + httpRequestWrapper.getOriginal().toString());
                return true;
            }
            if (e instanceof SocketTimeoutException) {
                // 连接超时
                logger.severe("【连接重试】 连接超时 重试:" + httpRequestWrapper.getOriginal().toString());
                return true;
            }
            if (e instanceof SSLException) {
                logger.severe("【连接重试】 SSLException 不重试:" + httpRequestWrapper.getOriginal().toString());
                return false;
            }
            logger.severe("未知IOException异常:" + e.getMessage());
            HttpClientContext context = HttpClientContext.adapt(httpContext);
            HttpRequest request = context.getRequest();
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                logger.severe("【连接重试】 请求不是关闭连接的请求 重试:" + httpRequestWrapper.getOriginal().toString());
                //如果请求不是关闭连接的请求
                return true;
            }
            logger.severe("【连接重试】 不重试:" + httpRequestWrapper.getOriginal().toString());
            return false;
        };
    }

    public static HttpXHelper custom() {
        return new HttpXHelper();
    }

    /**
     * 构建一个 httpClient
     *
     * @return
     */
    public HttpXHelper build() {

        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        if (cm != null) {
            httpClientBuilder.setConnectionManager(cm);
        } else {
            PoolingHttpClientConnectionManager defaultCm = new PoolingHttpClientConnectionManager();
            // 最大连接数
            defaultCm.setMaxTotal(1024);
            // 每条路线的最大连接数
            defaultCm.setDefaultMaxPerRoute(512);
            cm = defaultCm;
        }

        if (requestConfig != null) {
            httpClientBuilder.setDefaultRequestConfig(requestConfig);
        }
        if (keepAliveStrategy != null) {
            httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
        }
        if (proxy != null) {
            httpClientBuilder.setProxy(proxy);
        }
        if (openRetry && retryInterval == 0 && retryHandler != null) {
            httpClientBuilder.setRetryHandler(retryHandler);
        }
        if (openRetry && retryInterval > 0 && serviceUnavailStrategy != null) {
            httpClientBuilder.setServiceUnavailableRetryStrategy(serviceUnavailStrategy);
        }

        httpClient = httpClientBuilder.build();
        closeExpiredConnectionsPeriodTask(5);
        return this;
    }

    //    ConnectionKeepAliveStrategy
    private void closeExpiredConnectionsPeriodTask(int timeUnitBySecond) {
        InteriorThreadPoolFactory.getGeneral().execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(timeUnitBySecond);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PoolStats poolStats = cm.getTotalStats();
                logger.info("PoolingHttpClientConnectionManager TotalStats:  Leased -> " + poolStats.getLeased() +
                        ",Available -> " + poolStats.getAvailable() + ",Pending -> " + poolStats.getPending() + ",Max -> " + poolStats.getMax());
                //关闭过期连接,就是 ConnectionKeepAliveStrategy 设置的保活时间的连接
                cm.closeExpiredConnections();
                //关闭闲置连接
                cm.closeIdleConnections(10, TimeUnit.SECONDS);
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
    public GeneralResultVo<String> reqHolder(String url, String headParams, String bodyParams, HttpMethod httpMethod, MediaType mediaType) {
        return reqHolder(httpClient, url, headParams, bodyParams, httpMethod, mediaType);
    }

    public GeneralResultVo<String> reqHolderGet(String url, String headParams, String bodyParams) {
        return reqHolder(httpClient, url, headParams, bodyParams, HttpMethod.GET, null);
    }

    public GeneralResultVo<String> reqHolderGet(String url, String bodyParams) {
        return reqHolder(httpClient, url, null, bodyParams, HttpMethod.GET, null);
    }

    public GeneralResultVo<String> reqHolderPost(String url, String headParams, String bodyParams, MediaType mediaType) {
        return reqHolder(httpClient, url, headParams, bodyParams, HttpMethod.POST, mediaType);
    }

    public GeneralResultVo<String> reqHolderPost(String url, String bodyParams, MediaType mediaType) {
        return reqHolder(httpClient, url, null, bodyParams, HttpMethod.POST, mediaType);
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
    public static GeneralResultVo<String> reqHolder(CloseableHttpClient httpClient, String url, String headParams, String bodyParams, HttpMethod httpMethod, MediaType mediaType) {
        try {
            logger.info("HttpXHelper:reqHolder begin url-> " + url + ", headParams-> " + headParams + ",bodyParams -> " + bodyParams
                    + ", httpMethod -> " + httpMethod.name() + ", mediaType -> " + mediaType);
            Map<String, String> map = JSONObject.parseObject(bodyParams, HashMap.class);
            String resultString = null;
            if (httpMethod == HttpMethod.GET) {
                resultString = doGet(httpClient, url, headParams, map);
            } else {
                if (mediaType == MediaType.JSON) {
                    resultString = doPostJson(httpClient, url, headParams, bodyParams);
                } else {
                    resultString = doPost(httpClient, url, headParams, map);
                }
            }
            logger.info("HttpXHelper:reqHolder end url-> " + url + ", result -> " + resultString);
            return GeneralResultVo.success(resultString);
        } catch (Exception ex) {
            logger.info("HttpXHelper:reqHolder end url-> " + url + ", result Exception -> ");
            ex.printStackTrace();
        }
        return GeneralResultVo.fail();
    }

    private static String execute(CloseableHttpClient httpClient, HttpUriRequest httpUriRequest, String headParams) throws IOException {
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
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
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
    private static String doGet(CloseableHttpClient httpClient, String url, String headParams, Map<String, String> param) throws URISyntaxException, IOException {
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
        return execute(httpClient, httpGet, headParams);
    }

    /**
     * 发送get请求，不带参数
     *
     * @param url 请求地址
     * @return
     */
    private static String doGet(CloseableHttpClient httpClient, String url) throws URISyntaxException, IOException {
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
    private static String doPost(CloseableHttpClient httpClient, String url, String headParams, Map<String, String> param) throws IOException {
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
    private static String doPost(CloseableHttpClient httpClient, String url) throws IOException {
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
    private static String doPostJson(CloseableHttpClient httpClient, String url, String headParams, String json) throws IOException {
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


    public HttpXHelper setCm(PoolingHttpClientConnectionManager cm) {
        this.cm = cm;
        return this;
    }

    public HttpXHelper setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
        this.keepAliveStrategy = keepAliveStrategy;
        return this;
    }

    public HttpXHelper setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public HttpXHelper setRetryHandler(HttpRequestRetryHandler retryHandler) {
        this.retryHandler = retryHandler;
        return this;
    }

    public HttpXHelper setServiceUnavailStrategy(ServiceUnavailableRetryStrategy serviceUnavailStrategy) {
        this.serviceUnavailStrategy = serviceUnavailStrategy;
        return this;
    }

    public HttpXHelper setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        return this;
    }

    /**
     * 是否重试
     *
     * @param openRetry
     * @return
     */
    public HttpXHelper setOpenRetry(boolean openRetry) {
        this.openRetry = openRetry;
        return this;
    }

    /**
     * 重试次数
     *
     * @param retryNum
     * @return
     */
    public HttpXHelper setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
        return this;
    }

    /**
     * 重试间隔时间
     *
     * @param retryInterval
     * @return
     */
    public HttpXHelper setRetryInterval(Long retryInterval) {
        this.retryInterval = retryInterval;
        return this;
    }
}