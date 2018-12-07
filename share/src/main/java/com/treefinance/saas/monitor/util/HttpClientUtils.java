package com.treefinance.saas.monitor.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.exception.RequestFailedException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * httpclient 调用工具类
 * Created by yh-treefinance on 2017/5/17.
 */
public class HttpClientUtils {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    // 连接池
    private static PoolingHttpClientConnectionManager connMgr;
    // 超时时间
    private static final int MAX_TIMEOUT = 3000;

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
    }

    /**
     * 获取默认配置
     *
     * @return
     */
    private static RequestConfig getConfig() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .setExpectContinueEnabled(true)
                .setStaleConnectionCheckEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .build();
        RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                .setSocketTimeout(MAX_TIMEOUT)
                .setConnectTimeout(MAX_TIMEOUT)
                .setConnectionRequestTimeout(MAX_TIMEOUT)
                .build();
        return requestConfig;
    }

    /**
     * 获取默认client
     *
     * @return
     */
    private static CloseableHttpClient getClient() {
        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler((e, n, c) -> {
            return false;
        }).build();
        return httpclient;
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, Object> params) {
        long start = System.currentTimeMillis();

        List<String> paramList = Lists.newArrayList();
        for (String key : params.keySet()) {
            paramList.add(key + "=" + params.get(key));
        }
        String apiUrl = url + (url.contains("?") ? "&" : "?") + Joiner.on("&").join(paramList);
        String result = null;
        CloseableHttpClient httpclient = getClient();
        CloseableHttpResponse response = null;
        int statusCode = 0;
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setConfig(getConfig());
            response = httpclient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException("request url=" + apiUrl + " failed , statusCode = " + statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
        } catch (IOException e) {
            throw new RequestFailedException("request url=" + url + " failed , statusCode = " + statusCode, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doGet completed: url={}, params={}, statusCode={} , cost {} ms ", url, JSON.toJSONString(params), statusCode, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP）
     *
     * @param url
     * @return
     */
    public static String doPost(String url) {
        return doPost(url, new HashMap<String, Object>());
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param url
     * @param params 参数map
     * @return
     */
    public static String doPost(String url, Map<String, Object> params) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getClient();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        try {
            httpPost.setConfig(getConfig());
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);

            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException("request url=" + url + " failed , statusCode = " + statusCode);
            }
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            throw new RequestFailedException("request url=" + url + " failed , statusCode = " + statusCode, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doPost completed: url={}, params={}, statusCode={} , cost {} ms ", url, JSON.toJSONString(params), statusCode, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return httpStr;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param url
     * @param json json对象
     * @return
     */
    public static String doPost(String url, Object json) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getClient();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        try {
            httpPost.setConfig(getConfig());
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);

            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException("request url=" + url + " failed , statusCode = " + statusCode);
            }
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            throw new RequestFailedException("request url=" + url + " failed , statusCode = " + statusCode, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doPost completed: url={}, json={}, statusCode={} , cost {} ms ", url, JSON.toJSONString(json), statusCode, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return httpStr;
    }

    /**
     * 关闭响应流
     *
     * @param response
     */
    public static void closeResponse(CloseableHttpResponse response) {
        if (response != null) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                logger.error(" closeResponse failed", e);
            }
        }
    }


    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param url
     * @param params json对象
     * @return
     */
    public static CloseableHttpResponse fullyPost(String url, Map<String, String> params, Map<String, String> headers, Map<String, String> cookieMap) throws IOException {
        URI uri = URI.create(url);
        // 设置cookie
        CookieStore cookieStore = new BasicCookieStore();
        if (cookieMap != null) {
            for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
                BasicClientCookie basicClientCookie = new BasicClientCookie(entry.getKey(), entry.getValue());
                basicClientCookie.setDomain(uri.getHost());
                basicClientCookie.setPath("/");
                cookieStore.addCookie(basicClientCookie);
            }
        }
        // 构建client
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(connMgr)
                .build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(getConfig());
        // 设置header
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        // 设置params
        String json = JSON.toJSONString(params);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);
        return httpClient.execute(httpPost);
    }
    /**
     * 发送 POST 请求（HTTP），JSON形式，Map header
     *
     * @param url
     * @param json json对象
     * @return
     */
    public static String doPostwithHeader(String url, Object json,Map<String, String> headers) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getClient();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        try {
            httpPost.setConfig(getConfig());
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");

            List<Header> headerList = Lists.newArrayList();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                Header header = new BasicHeader(entry.getKey(), entry.getValue());
                headerList.add(header);
            }
            Header[] headerArray = new Header[headerList.size()];
            headerArray = headerList.toArray(headerArray);
            httpPost.setEntity(stringEntity);
            httpPost.setHeaders(headerArray);
            response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                httpStr = EntityUtils.toString(entity, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException("request url=" + url + " failed , statusCode = " + statusCode);            }
        } catch (IOException e) {
            throw new RequestFailedException("request url=" + url + " failed , statusCode = " + statusCode, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doPost completed: url={}, json={}, statusCode={} ,result={}, cost {} ms ",
                        url, JSON.toJSONString(json), statusCode, httpStr, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return httpStr;
    }

}
