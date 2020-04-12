package com.music.player.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.music.player.R;
import com.music.player.base.BaseActivity;

public class Loading_Activity extends BaseActivity   {
    private final int time = 3000;
    private boolean lag = true;



    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.loading);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {//延时time秒后，将运行如下代码
                if(lag){
                    finish();
                    Toast.makeText(Loading_Activity.this , "欢迎使用Cool音乐！" , Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(Loading_Activity .this , Login.class);
                    Intent intent = new Intent(Loading_Activity .this , Login_Activity.class);
                    startActivity(intent);
                }
            }
        } , time);
    }
    @Override
    public void findView() {

    }
    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }
}
