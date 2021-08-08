package com.b0c0.common.utils;


import com.b0c0.common.factory.InteriorThreadPoolFactory;

public class HttpUtilsTest {

    public static void main(String[] args) {
        reqHolder();
    }

    public static void reqHolder() {
        int max = 100;
        System.out.println(HttpXHelper.custom().build().reqHolderGet("http://www.baidu.com", null, null).isSuccess());
        System.out.println(HttpXHelper.custom().build().reqHolderGet("http://www.baidu.com", null, null).isSuccess());
//        while ((max-- ) >0){
//            InteriorThreadPoolFactory.getGeneral().execute(()->{
//                System.out.println(HttpXHelper.custom().build().reqHolderGet("http://www.baidu.com", null, null).isSuccess());
//            });
//        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(HttpXHelper.custom().build().reqHolderGet("http://www.baidu.com", null, null).isSuccess());
    }
}