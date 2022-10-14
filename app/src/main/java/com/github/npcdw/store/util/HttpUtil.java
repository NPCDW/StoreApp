package com.github.npcdw.store.util;

import com.github.npcdw.store.config.GlobalConfig;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Http接口工具类
 */
public class HttpUtil {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static String post(String url, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(body, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("token", GlobalConfig.TOKEN)
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200 || response.body() == null) {
                return null;
            }
            return response.body().string();
        }
    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("token", GlobalConfig.TOKEN)
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200 || response.body() == null) {
                return null;
            }
            return response.body().string();
        }
    }

    public static String put(String url, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(body, JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .header("token", GlobalConfig.TOKEN)
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200 || response.body() == null) {
                return null;
            }
            return response.body().string();
        }
    }

    public static String delete(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .header("token", GlobalConfig.TOKEN)
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200 || response.body() == null) {
                return null;
            }
            return response.body().string();
        }
    }

}
