package com.cn.llh.utlis;

/**
 * Created by zfh on 2016/1/14.
 */
public class Constant {

    //短信
    public final static String APPKEY = "ee75ad56559c"; // 填写从短信SDK应用后台注册得到的APPKEY
    public final static String APPSECRET = "d04a5ec1beba40b3b5d78c6bf6ccfefa";// 填写从短信SDK应用后台注册得到的APPSECRET

    //短信运营商状态吗
    public final static String INVALID_MOBILE_PHONE = "467"; //无效手机号码
    public final static String INVALID_VERIFICATION_CODE = "468"; //无效验证码

    //等待时间
    public final static int WAITING_TIME_VERIFICATION = 60; //获取验证码等待时间
    public final static int THREAD_WAITING_TIME = 1000; //线程等待时间
    public final static String RETURN_SUCCESS = "SUCCESS"; //Android异步线程执行成功
    public final static String THREAD_CANCEL = "CANCEL"; //Android异步线程停止


    //-----------------------------正则表达式---------------------
    //手机格式
    public static final String MATCH_MOBILE_PHONE = "^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";
}
