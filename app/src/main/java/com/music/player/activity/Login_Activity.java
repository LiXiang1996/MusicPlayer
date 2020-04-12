package com.music.player.activity;

        import android.os.Bundle;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.SharedPreferences.Editor;
        import android.view.Menu;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.music.player.R;
        import com.music.player.base.BaseActivity;

public class Login_Activity extends BaseActivity {
    //声明需要的组件
    private  Button login,exit,reg;
    private  EditText   username,password;
    private SharedPreferences share;//声明SharedPreferences
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_test);
        intiview();//初始化视图，寻找id
        saveuser();//先保存一个数据admin 123456
        exit.setOnClickListener(new Listenerimp());//退出的监听事件
        reg.setOnClickListener(new RegListenerimp());//注册的监听事件
        //登陆的事件监听处理内部类
        login.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        //获取输入的信息
                        String name=username.getText().toString();
                        String pass=password.getText().toString();
                //判断输入信息是否为空
                if(name.trim().equals("") || pass.trim().equals("")) {
                    Toast.makeText(Login_Activity.this, "用户名和密码不能为空", Toast.LENGTH_LONG).show();
                }
                //获取保存文件中的用户名和密码
                String savedUsername = share.getString("username","");
                String savedPassword = share.getString("password","");
                //查看输入的密码和名字是否一致
                if(name.trim().equals(savedUsername) && pass.trim().equals(savedPassword)) {
                    Toast.makeText(Login_Activity.this, "恭喜，亲，用户名和密码都正确！", Toast.LENGTH_LONG).show();

                    //成功登陆，进入LoginokActivity界面
                    Intent intent=new Intent(Login_Activity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    //错误的话
                    Toast.makeText(Login_Activity.this, "用户名或者密码错误，请确认信息或者去注册", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {

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

    private class Listenerimp implements  OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            finish();//结束一个Activity
        }
    }
    private class  RegListenerimp implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //定义两个字符串常量，并获取信息
            final String nam=username.getText().toString();
            final String pas=password.getText().toString();
            //判读信息是否空
            if(nam.trim().equals("") || pas.trim().equals("")) {
                Toast.makeText(Login_Activity.this, "注册时，用户名和密码都不能为空", Toast.LENGTH_LONG).show();
                return;//为空就会返回
            }
            //进入注册的Dialog
            Dialog dialog=new AlertDialog.Builder(Login_Activity.this)
                    .setTitle("注册")
                    .setMessage("你确定注册信息吗？")
                    .setPositiveButton("确定", new  DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            //保存输入的信息 	 Editor 别忘了		edit.commit();提交
                            share=getSharedPreferences("info",Activity.MODE_PRIVATE);
                            Editor edit=share.edit();
                            edit.putString("username", nam);
                            edit.putString("password", pas);
                            edit.commit();
                            //提示成功注册
                            Toast.makeText(Login_Activity.this, "恭喜，注册成功", Toast.LENGTH_LONG).show();

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    }).create();//创建一个dialog
            dialog.show();//显示对话框，否者不成功
        }

    }


    //实现写一个admin 123456的用户
    private void saveuser() {
        // TODO Auto-generated method stub
        share=getSharedPreferences("info",Activity.MODE_PRIVATE);
        Editor edit=share.edit();
        edit.putString("username", "admin");
        edit.putString("password", "123456");
        edit.commit();
    }


    private void intiview() {
        // TODO Auto-generated method stub
        login=(Button)findViewById(R.id.login);
        exit=(Button)findViewById(R.id.exit);
        reg=(Button)findViewById(R.id.reg);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
    }
}
