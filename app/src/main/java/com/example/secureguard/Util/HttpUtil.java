package com.example.secureguard.Util;

import android.os.Message;
import android.util.Log;
import okhttp3.*;

import java.io.IOException;

public class HttpUtil {
    public static void sendokHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void postHttpRequest(String address, String item, String data, okhttp3.Callback callback) {
        String url = address + "?item=" + item + "&data=" + data;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Log.i("TAG3", "postHttpRequest: " + url);
        client.newCall(request).enqueue(callback);
    }

//    private void testOkhttpPost(String address, String item, String data, okhttp3.Callback callback) {
//
//
//        OkHttpClient okHttpClient = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("item", item)
//                .add("data", data)
//                .build();
//
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(address)
//                .post(body)
//                .build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(callback);
//    }
}
