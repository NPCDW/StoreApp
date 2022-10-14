package com.github.npcdw.store.config;

import android.content.Context;
import android.util.Log;

import com.github.npcdw.store.util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class GlobalConfig {
    public static String INTERFACE_API_BASE_URL = "";
    public static String MINIO_API_BASE_URL = "";
    public static String MINIO_BUCKET = "";
    public static String MINIO_USERNAME = "";
    public static String MINIO_PASSWORD = "";
    public static String TOKEN = "";

    /**
     * 读取配置文件
     */
    public static void getConfig(Context context){
        try (InputStream in = context.getAssets().open("app_config.json");
             InputStreamReader isr = new InputStreamReader(in);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder result = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                result.append(line);
            }
            Map<String, Object> map = JsonUtil.parseObject(result.toString());
            GlobalConfig.INTERFACE_API_BASE_URL = map.get("interface_api_base_url").toString();
            GlobalConfig.MINIO_API_BASE_URL = map.get("minio_api_base_url").toString();
            GlobalConfig.MINIO_BUCKET = map.get("minio_bucket").toString();
            GlobalConfig.MINIO_USERNAME = map.get("minio_username").toString();
            GlobalConfig.MINIO_PASSWORD = map.get("minio_password").toString();
            GlobalConfig.TOKEN = map.get("token").toString();
        } catch (IOException e) {
            Log.e("Config", "获取配置文件出错", e);
        }
    }
}
