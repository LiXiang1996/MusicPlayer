package com.music.player.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.music.player.R;
import com.music.player.base.BaseActivity;

public class User_Data extends BaseActivity implements View.OnClickListener {
    private Button back;
    private TextView user;
    private TextView code;
    @Override
    public void setContentView(Bundle savedInstanceState) {
         setContentView(R.layout.user_database);
         back=(Button)findViewById(R.id.back);
         user=(TextView)findViewById(R.id.edit_user);
         code=(TextView) findViewById(R.id.edit_code);

//         Intent intent=getIntent();
//         String string1=intent.getStringExtra("user");
////         String string2= intent.getStringExtra("code");
////         user.setText(" "+string1);
////         code.setText(" "+string2);
//        user.setText(intent.getStringExtra("user"));
//        code.setText(intent.getStringExtra("code"));

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
    back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                Intent intent=new Intent(User_Data.this,MainActivity.class);
                startActivity(intent);
        }
    }
}
