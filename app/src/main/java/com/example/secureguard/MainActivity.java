package com.example.secureguard;


import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.secureguard.Util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public String ip="";
    Intent intent;
    private EditText edit_id;
    private EditText edit_pwd;
    public EditText edit_ip;
    String num, pwd, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView btn_sign_up = findViewById(R.id.btn_sign_up);
        Button btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_up.setOnClickListener(this);
        btn_sign_in.setOnClickListener(this);
        edit_id = findViewById(R.id.edit_id);
        edit_pwd = findViewById(R.id.edit_password);
        edit_ip = findViewById(R.id.edit_ip);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //注册逻辑处理
            case R.id.btn_sign_in:
                pwd = edit_pwd.getText().toString();
                num = edit_id.getText().toString();
                ip=edit_ip.getText().toString();
                if(ip.equals("")){
                    Toast.makeText(MainActivity.this, "请填写ip地址！", Toast.LENGTH_SHORT);
                    break;
                }
                //拼接地址
                //http://10.17.128.37:8080/toLogin.do?usernum=18166376678&password=123456
                address = "http://"+ip+":8080/myweb/toLogin.do??usernum=" + num + "&password=" + pwd;
                Log.i("TAG", "onClick: " + address);
                HttpUtil.sendokHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "网络出错！", Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseDate = response.body().string();
                        JSONObject jsonObject = null;
                        String status = null;
                        try {
                            jsonObject = new JSONObject(responseDate);
                            Log.d("TAG", "onResponse: " + jsonObject.toString());
                            status = jsonObject.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if ("0".equals(status)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "用户ID或密码错误！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (num.equals("admin")) {
                                        intent = new Intent(MainActivity.this, AdminActivity.class);
                                    } else {
                                        intent = new Intent(MainActivity.this, UserActivity.class);
                                    }
                                    intent.putExtra("num", num);
                                    intent.putExtra("ip", ip);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.btn_sign_up:
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("ip", ip);
                startActivity(intent);
                finish();
                break;
        }
    }
}