package com.example.zhihu;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Client {
    private static OkHttpClient instance;

    private Client(){
    }

    public static void sendRequest(String url,okhttp3.Callback callback){
        if (instance==null){
            instance=new OkHttpClient.Builder().build();
        }
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        instance.newCall(request).enqueue(callback);
    }
}
