package com.cn.llh.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cn.llh.R;
import com.cn.llh.utlis.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


/**
 * Created by zfh on 2016/1/18.
 * 注册页面
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    Button sendCode;//发送验证码
    EditText mphone;//手机
    EditText edAutoCode;//验证码
    private Context context; //上下文对象
    private boolean asyncIsOver = false; //异步线程是否结束
    private VerificationCodeProgressAsyncTask asyncTask; //异步消息对象



    private static final int VERIFICATION_AUTHENTICATE_SUCCESS = 0x20; //验证码认证成功
    private final static int INVALID_MOBILE_PHONE = 467; //请求校验验证码频繁（5分钟内同一个appkey的同一个号码最多只能校验三次）
    private final static int INVALID_VERIFICATION_CODE = 468; //无效验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();//初始化
        initlistener();//初始化点击
        initSMSDK(); //初始化短信服务
    }

    private void init() {
        mphone = (EditText) findViewById(R.id.phone);
        edAutoCode = (EditText) findViewById(R.id.autoCode);
        sendCode = (Button) findViewById(R.id.btnSendCode);
        context = this;
    }

    private void initlistener() {
        sendCode.setOnClickListener(this);
    }
    public void submit(View v){//提交
        checkInfo();//检查信息
    }

    private void checkInfo() {
        String autoCode = edAutoCode.getText().toString().trim();
        String mobilePhone =mphone.getText().toString().trim();
        if (autoCode!=null&&autoCode.equals("")){
            Toast.makeText(context,"验证码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (mobilePhone!=null&&mobilePhone.equals("")){
            Toast.makeText(context,"手机号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //验证手机号码格式是否正确
        Pattern pattern = Pattern.compile(Constant.MATCH_MOBILE_PHONE); //手机号匹配模式
        Matcher matcher = pattern.matcher(mobilePhone);
        if(!matcher.matches()) {
            Toast.makeText(context,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
            return;
        }
        //校验验证码
        SMSSDK.submitVerificationCode("86", mobilePhone, autoCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSendCode:
                requestVerificationCode(); //请求验证码
                break;
        }
    }

    /**
     * 初始化短信
     */
    private void initSMSDK() {
        SMSSDK.initSDK(this, Constant.APPKEY, Constant.APPSECRET);
        EventHandler eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message message;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        message = Message.obtain();
                        message.what = VERIFICATION_AUTHENTICATE_SUCCESS;
                        handler.sendMessage(message);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                    } else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    //transitionDialog.dismissDialog();
                    //这里不是UI线程，如果要更新或者操作UI，要调用UI线程
                    String dataStr = data.toString();
                    String str = dataStr.substring(dataStr.indexOf(":")+1).trim();
                    try {
                        JSONObject dataJson = new JSONObject(str);
                        int status = dataJson.getInt("status");
                        message = Message.obtain();
                        switch (status) {
                            case INVALID_MOBILE_PHONE: //验证码请求频繁
                                message.what = Integer.valueOf(Constant.INVALID_MOBILE_PHONE);
                                handler.sendMessage(message);
                                break;
                            case INVALID_VERIFICATION_CODE: //无效验证码
                                message.what = Integer.valueOf(Constant.INVALID_VERIFICATION_CODE);
                                handler.sendMessage(message);
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    /**
     * 请求验证码
     */
    private void requestVerificationCode() {
        String mobilePhone =mphone.getText().toString().trim();
        if (mobilePhone!=null&&mobilePhone.equals("")){
            Toast.makeText(context,"手机号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //验证手机号码格式是否正确
        Pattern pattern = Pattern.compile(Constant.MATCH_MOBILE_PHONE); //手机号匹配模式
        Matcher matcher = pattern.matcher(mobilePhone);
        if(!matcher.matches()) {
            Toast.makeText(context,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
            return;
        }
        //开启获取验证码按钮倒计时
        asyncTask = new VerificationCodeProgressAsyncTask();
        asyncTask.execute(Constant.WAITING_TIME_VERIFICATION);
        //向短信服务运营商请求短信验证码
        SMSSDK.getVerificationCode("86", mobilePhone);
    }
    /**
     * 线程通讯
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VERIFICATION_AUTHENTICATE_SUCCESS: //验证码认证成功
                    Toast.makeText(context,"验证成功",Toast.LENGTH_SHORT).show();
                    //showToast(context, "验证成功");
                    break;
                case INVALID_VERIFICATION_CODE: //无效验证码
                    Toast.makeText(context,"无效验证码",Toast.LENGTH_SHORT).show();
                    break;
                case INVALID_MOBILE_PHONE: //无效手机号
                    Toast.makeText(context,"5分钟内同一个号码最多只能校验三次",Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }
    };
    /**
     *  AsyncTask定义了三种泛型类型 Params，Progress和Result。
     *  Params 启动任务执行的输入参数，比如HTTP请求的URL。
     *  Progress 后台任务执行的百分比。
     *  Result 后台执行任务最终返回的结果，比如String。
     */
    private class VerificationCodeProgressAsyncTask extends AsyncTask<Integer, Integer, String> {
        /**
         * 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPreExecute() {
            setVerificationCodeButtonDisable(); //设置获取验证码按钮为灰色不可用
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数
         * 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            if(isCancelled()) { //判断线程是否取消
                //已取消
                return Constant.THREAD_CANCEL; //返回CANCEL标志
            }
            int waitingTime = params[0].intValue(); //异步等待时间
            try {
                for(int i = waitingTime; i >= 0 ; i--) {
                    publishProgress(i);
                    Thread.sleep(Constant.THREAD_WAITING_TIME); //线程等待
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Constant.RETURN_SUCCESS; //线程执行完毕
        }

        /**
         *执行结束调用
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            asyncIsOver = true;
            setVerificationCodeButtonEnable(); //设置获取验证码按钮为橙色可用
        }

        /**
         * 取消一个正在执行的任务,onCancelled方法将会被调用
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
            setVerificationCodeButtonEnable(); //设置获取验证码按钮为橙色可用
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            int cusTime = values[0].intValue();
            if(cusTime == 0) {
                sendCode.setText("重新发送");
            } else {
                sendCode.setText("重新发送("+String.valueOf(cusTime)+")");
            }

        }
    }
    /**
     * 设置获取验证码按钮不可用
     */
    private void setVerificationCodeButtonDisable() {
        sendCode.setEnabled(false);
        sendCode.setBackgroundResource(R.drawable.border_corner_login);
    }
    /**
     * 设置获取验证码按钮可用
     */
    private void setVerificationCodeButtonEnable() {
        sendCode.setEnabled(true);
        sendCode.setBackgroundResource(R.drawable.border_corner_login_enable);
    }
}
