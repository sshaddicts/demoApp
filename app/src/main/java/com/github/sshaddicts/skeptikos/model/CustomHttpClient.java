package com.github.sshaddicts.skeptikos.model;

import android.util.Base64;

import com.github.sshaddicts.skeptikos.fragments.CustomView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class CustomHttpClient extends WebSocketListener{

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private final OkHttpClient client;
    private Request request;
    CustomView view;

    public CustomHttpClient(CustomView view) {
        this.imageData = imageData;
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .build();
        this.view = view;
    }

    long length;
    byte[] imageData;

    public void run(byte[] imageData) throws Exception {

        String head="{\"procedure\":\"process.image\"," +
                "\"args\":[]," +
                "\"kwargs\":{\"image\":\"";
        String tail="\",\"token\":\"anonymous\"}}";

        //String s = "{\"procedure\":\"process.image\", \"args\":[], \"kwargs\": { \"image\":\"\", \"token\":\"anonymous\" }}";
        String image = new String(Base64.encode(imageData, Base64.DEFAULT));
        //System.out.println(image);

        JSONObject json = new JSONObject();

        json.put("procedure", "process.image");
        json.put("args", new JSONArray());

        JSONObject kwargs = new JSONObject();

        kwargs.put("token", "anonymous");
        kwargs.put("image", image);
        json.put("kwargs", kwargs);

        System.out.println(json.toString());

        MediaType type = MediaType.parse("application/json; charset=utf-8");
        final Request request = new Request.Builder()
                .url("http://192.168.0.111:7778/call")
                .post(RequestBody.create(type, json.toString()))
                .build();

        System.out.println(request.headers());
        System.out.println(request.toString());



        Thread t1 = new Thread(){
            @Override
            public void run() {
                System.out.println("start");
                try {
                    Response execute = client.newCall(request).execute();
                    view.receiveData(execute);
                } catch (IOException e) {
                    view.receiveError(e);
                }
            }
        };

        t1.start();
    }
}