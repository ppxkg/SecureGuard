package com.example.secureguard;

import android.app.AlertDialog;
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
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {
    Intent intent;
    private TextView welcome;
    private Toolbar mtoolbar;
    private ListView mlistView;
    private List<Bulletin> bulletins = new ArrayList<>();
    private List<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.activity_user);
        initView();
        String ip=intent.getStringExtra("ip");
        welcome.setText("欢迎您，" + intent.getStringExtra("num"));
        setSupportActionBar(mtoolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出代码
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(UserActivity.this, "退出", Toast.LENGTH_SHORT).show();
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
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UserActivity.this, R.layout.support_simple_spinner_dropdown_item, items);
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

//    {
//        try {
//            mSocket = IO.socket("http://1.15.115.24:8000/");
//        } catch (URISyntaxException e) {
//        }
//    }


    private void initView() {
        welcome = findViewById(R.id.welcome);
//        btn_openTheDoor = findViewById(R.id.btn_openTheDoor);
        mtoolbar = findViewById(R.id.toolbar);
        mlistView = findViewById(R.id.ListView);
    }

//    private void SendSocket() {
//        btn_openTheDoor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSocket.emit("openDoor", intent.getStringExtra("num"));
//            }
//        });
//    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle("标题：" + bulletins.get(position).getItem());
        builder.setMessage("内容：\n" + bulletins.get(position).getData() + "\n时间：\n" + bulletins.get(position).getTime());
        builder.show();
    }
}