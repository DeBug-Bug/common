package com.b0c0.common.utils;


import com.b0c0.common.factory.InteriorThreadPoolFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpUtilsTest {

    public static void main(String[] args) {
        reqHolder();
    }

    public static void reqHolder() {

        PoolingHttpClientConnectionManager cm1 = new PoolingHttpClientConnectionManager();
        // 最大连接数
        cm1.setMaxTotal(1024);
        // 每条路线的最大连接数
        cm1.setDefaultMaxPerRoute(512);
        PoolingHttpClientConnectionManager cm2 = new PoolingHttpClientConnectionManager();
        // 最大连接数
        cm2.setMaxTotal(1023);
        // 每条路线的最大连接数
        cm2.setDefaultMaxPerRoute(512);
//        HttpXHelper httpXHelper1= HttpXHelper.custom().setCm(cm1).build();
        HttpXHelper httpXHelper2= HttpXHelper.custom().setCm(cm2).setAutoCloseUselessConnections(false).build();
        HttpXHelper httpXHelper3= HttpXHelper.custom().setCm(cm2).setAutoCloseUselessConnections(false).build();
//        System.out.println(httpXHelper1.reqHolderGet("http://www.baidu.com", null, null).isSuccess());
        HttpXHelper.closeExpiredConnectionsPeriodTask(cm2,5);
        System.out.println(httpXHelper2.reqHolderGet("http://www.baidu.com", null, null).isSuccess());
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println(httpXHelper2.reqHolderGet("http://www.b0c0.com", null, null).isSuccess());
        System.out.println(httpXHelper3.reqHolderGet("http://www.b0c0.com", null, null).isSuccess());

//        int max = 10;
//        while ((max-- ) >0){
//            InteriorThreadPoolFactory.getGeneral().execute(()->{
//                System.out.println(httpXHelper2.reqHolderGet("http://www.baidu.com", null, null).isSuccess());
//            });
//        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(HttpXHelper.custom().build().reqHolderGet("http://www.baidu.com", null, null).isSuccess());
    }
}