package com.example.secureguard;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.example.secureguard.Util.HttpUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.secureguard.Util.HttpUtil.postHttpRequest;

public class AdminActivity extends AppCompatActivity {
    private Button transition;
    private Button push_bulletin;
    private Toolbar mtoolbar;
    private View mView;
    private ListView mlistView;
    private EditText et_item;
    private EditText et_data;
    private List<Bulletin> bulletins = new ArrayList<>();
    private List<String> items = new ArrayList<>();
    public String ip;
    public String num;
//    {
//        try {
//            mSocket = IO.socket("http://1.15.115.24:8000/");
//        } catch (URISyntaxException e) {
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        init();
        ip=getIntent().getStringExtra("ip");
        num=getIntent().getStringExtra("num");
        push_bulletin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                push(v);
            }
        });
        transition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,personActivity.class);
                intent.putExtra("ip",ip);
                intent.putExtra("num",num);
                startActivity(intent);
            }
        });
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出代码
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("ip",ip);
                startActivity(intent);
                Toast.makeText(AdminActivity.this, "退出", Toast.LENGTH_SHORT).show();
            }
        });
//        mSocket.connect();
        HttpUtil.sendokHttpRequest("http://"+ip+":8080/myweb/getBulletin.do", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseDate = response.body().string();
                parseJSONwithJSONObject(responseDate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AdminActivity.this, R.layout.support_simple_spinner_dropdown_item, items);
                        mlistView.setAdapter(arrayAdapter);
                    }
                });
            }
        });
//        SendSocket();
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowBulletin(view, position);
            }
        });
    }

    //初始化
    public void init() {
        mtoolbar = findViewById(R.id.toolbar);
        mlistView = findViewById(R.id.adListView);
        transition = findViewById(R.id.btn_getPerson);
        mtoolbar.setTitle("欢迎您，管理员");
        push_bulletin = findViewById(R.id.btn_push_bulletin);
        setSupportActionBar(mtoolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

//    private void SendSocket() {
//        transition.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSocket.emit("openDoor", "管理员");
//            }
//        });
//    }

    // 发出公告的弹出框
    public void push(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("输入公告");
        mView = View.inflate(getApplicationContext(), R.layout.postbulletin, null);
        builder.setView(mView);
        et_item = mView.findViewById(R.id.et_item);
        et_data = mView.findViewById(R.id.et_data);
        builder.setPositiveButton("发布", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //此处为发送公告的方法
                postHttpRequest("http://"+ip+":8080/myweb/setBulletin.do", et_item.getText().toString(), et_data.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("TAG1", "onFailure: ");
//                        Toast.makeText(AdminActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
//                        Toast.makeText(AdminActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        Log.d("TAG2", "onResponse: ");
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public void parseJSONwithJSONObject(String jsonDate) {
        try {
            JSONArray jsonArray = new JSONArray(jsonDate);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String data = jsonObject.getString("data");
                String time = jsonObject.getString("time");
                String item = jsonObject.getString("item");
                Bulletin bulletin = new Bulletin(data, time, item);
                bulletins.add(bulletin);
                items.add(item);
                Log.d("print1", "parseJSONwithJSONObject: " + bulletin.getData());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void ShowBulletin(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("标题：" + bulletins.get(position).getItem());
        builder.setMessage("内容：\n" + bulletins.get(position).getData() + "\n时间：\n" + bulletins.get(position).getTime());
        builder.show();
    }
}