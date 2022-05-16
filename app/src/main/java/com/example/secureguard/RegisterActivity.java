package com.example.secureguard;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.secureguard.Util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    public Button register;
    public EditText user;
    public EditText word;
    public EditText telnum;
    public RequestBody registerRequestBody;
    public String userName;
    public String passWord;
    public String address;
    public String num;
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ip=getIntent().getStringExtra("ip");
        register = (Button) findViewById(R.id.btn_register);
        user = (EditText) findViewById(R.id.user);
        word = (EditText) findViewById(R.id.password_register);
        telnum = (EditText) findViewById(R.id.telnum_register);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //注册逻辑处理
            case R.id.btn_register:
                userName = user.getText().toString();
                passWord = word.getText().toString();
                num = telnum.getText().toString();
                //拼接地址
                address = "http://"+ip+":8080/myweb/toRegister.do?usernum=" + num + "&username=" + userName + "&password=" + passWord;
                System.out.println(address);
                HttpUtil.sendokHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "网络出错！", Toast.LENGTH_SHORT);
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
                            status = jsonObject.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(status);
                        if ("0".equals(status)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "注册失败！请重新尝试...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
                break;
        }
    }
}