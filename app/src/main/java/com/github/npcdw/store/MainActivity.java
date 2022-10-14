package com.github.npcdw.store;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.npcdw.store.config.GlobalConfig;
import com.github.npcdw.store.util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalConfig.getConfig(MainActivity.this);

        requestPermission();

        Button goods_list = findViewById(R.id.goods_list);
        goods_list.setOnClickListener(view -> {
            Intent intent = new Intent(this, GoodsListActivity.class);
            startActivity(intent);
        });

        Button goods_storage = findViewById(R.id.goods_storage);
        goods_storage.setOnClickListener(view -> {
            Intent intent = new Intent(this, GoodsAddActivity.class);
            startActivity(intent);
        });

        Button goods_query = findViewById(R.id.goods_query);
        goods_query.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScanQRCodeActivity.class);
            startActivity(intent);
        });
    }

    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_PERMISSION_CODE2 = 2;

    public void requestPermission() {
        int permission_read = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission_read != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
        int permission_write = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission_write != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            Log.i("", "request permission success");
        }
        if (requestCode == REQUEST_PERMISSION_CODE2) {
            Log.i("", "request permission success");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}