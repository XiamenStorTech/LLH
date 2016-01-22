package com.cn.llh.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cn.llh.R;

/**
 * Created by zfh on 2016/1/18.
 * 登录界面
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    TextView teForgetPassword; //忘记密码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();//初始化
        initlistener();//初始化点击
    }
    


    private void init() {
        teForgetPassword = (TextView) findViewById(R.id.teforget);
    }

    private void initlistener() {
        teForgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.teforget:
                Intent intent=new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}
