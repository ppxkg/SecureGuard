package com.example.secureguard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secureguard.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class personActivity extends AppCompatActivity {
    Intent intent;
    private TextView welcome;
    private Toolbar mtoolbar;
    private ListView mlistView;
    private List<Person> persons = new ArrayList<>();
    private List<String> items_plus = new ArrayList<>();
    public String ip;
    public String num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.persons);
        initView();
        ip=intent.getStringExtra("ip");
        num=intent.getStringExtra("num");
        welcome.setText("欢迎您，" + intent.getStringExtra("num"));
        setSupportActionBar(mtoolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出代码
                Intent intent=new Intent(getApplicationContext(), AdminActivity.class);
                intent.putExtra("ip",ip);
                intent.putExtra("num",num);
                startActivity(intent);
                Toast.makeText(personActivity.this, "退出", Toast.LENGTH_SHORT).show();
            }
        });
//        mSocket.connect();
        HttpUtil.sendokHttpRequest("http://"+ip+":8080/myweb/getPerson.do", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseDate = response.body().string();
                parseJSONwithJSONObjectPerson(responseDate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(personActivity.this, R.layout.support_simple_spinner_dropdown_item, items_plus);
                        mlistView.setAdapter(arrayAdapter);
                    }
                });
            }
        });
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowBulletin(view, position);
            }
        });
    }
    private void initView() {
        welcome = findViewById(R.id.welcome);
        mtoolbar = findViewById(R.id.toolbar);
        mlistView = findViewById(R.id.ListView);
    }
    public void parseJSONwithJSONObjectPerson(String jsonDate) {
        try {
            JSONArray jsonArray = new JSONArray(jsonDate);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String time = jsonObject.getString("time");
                String temperature = jsonObject.getString("temperature");
                Person person=new Person();
                person.setName(name);
                person.setTime(time);
                person.setTemperature(temperature);
                persons.add(person);
                items_plus.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void ShowBulletin(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(personActivity.this);
        builder.setTitle("标题：" + persons.get(position).getName());
        builder.setMessage("姓名：\n"+persons.get(position).getName()+"\n体温：\n" + persons.get(position).getTemperature() + "\n时间：\n" + persons.get(position).getTime());
        builder.show();
    }
}