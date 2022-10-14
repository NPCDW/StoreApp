package com.github.npcdw.store;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.npcdw.store.databinding.ActivityGoodsDetailBinding;
import com.github.npcdw.store.entity.Goods;
import com.github.npcdw.store.config.GlobalConfig;
import com.github.npcdw.store.util.DateTimeUtil;
import com.github.npcdw.store.util.HttpUtil;
import com.github.npcdw.store.util.JsonUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class GoodsDetailActivity extends AppCompatActivity {

    private ActivityGoodsDetailBinding binding;
    private String qrcode;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGoodsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        qrcode = intent.getStringExtra("qrcode");
        id = intent.getIntExtra("id", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FloatingActionButton fab = binding.fab;

        new Thread(() -> {
            try {
                String url;
                if (id != 0) {
                    url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/getInfo/" + id;
                } else {
                    url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/getInfoByQRCode?qrcode=" + qrcode;
                }
                String response = HttpUtil.get(url);
                if (response == null) {
                    Snackbar.make(binding.getRoot(), "服务器连接失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                Map<String, Object> map = JsonUtil.parseObject(response);
                if (map == null || !Boolean.parseBoolean(map.get("success").toString())) {
                    Snackbar.make(binding.getRoot(), "获取失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                GoodsDetailActivity.this.runOnUiThread(() -> {
                    fab.setVisibility(View.VISIBLE);
                    if (map.get("data") == null) {
                        findViewById(R.id.goods_detail_goods_no_exist_layout).setVisibility(View.VISIBLE);
                        fab.setImageResource(R.drawable.ic_baseline_add_24);
                        fab.setOnClickListener(view -> {
                            Intent intent2 = new Intent(this, GoodsAddActivity.class);
                            intent2.putExtra("qrcode", qrcode);
                            startActivity(intent2);
                        });
                        return;
                    }
                    Goods goods = null;
                    try {
                        goods = JsonUtil.parseObject(JsonUtil.toJsonString(map.get("data")), Goods.class);
                    } catch (Exception e) {
                        Log.e("JsonUtil", "Jackson序列化失败", e);
                    }
                    if (goods == null) {
                        return;
                    }
                    findViewById(R.id.goods_detail_goods_exist_layout).setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.ic_baseline_edit_24);
                    Goods finalGoods = goods;
                    fab.setOnClickListener(view -> {
                        Intent intent2 = new Intent(this, GoodsAddActivity.class);
                        intent2.putExtra("id", finalGoods.getId().toString());
                        intent2.putExtra("name", finalGoods.getName());
                        intent2.putExtra("cover", finalGoods.getCover());
                        intent2.putExtra("price", finalGoods.getPrice().toString());
                        intent2.putExtra("unit", finalGoods.getUnit());
                        intent2.putExtra("qrcode", finalGoods.getQrcode());
                        startActivity(intent2);
                    });
                    if (StringUtils.isNotBlank(goods.getCover())) {
                        ImageView imageView = findViewById(R.id.goods_detail_cover);
                        imageView.setBackground(null);
                        Glide.with(this).load(GlobalConfig.MINIO_API_BASE_URL + goods.getCover()).into(imageView);
                    }
                    ((TextView) findViewById(R.id.goods_detail_goods_name)).setText(goods.getName());
                    ((TextView) findViewById(R.id.goods_detail_goods_price)).setText(goods.getPrice() == null ? "" : "￥" + goods.getPrice().toString());
                    ((TextView) findViewById(R.id.goods_detail_unit)).setText(goods.getUnit());
                    ((TextView) findViewById(R.id.goods_detail_create_time)).setText(DateTimeUtil.formatDateTime(goods.getCreateTime()));
                    ((TextView) findViewById(R.id.goods_detail_update_time)).setText(DateTimeUtil.formatDateTime(goods.getUpdateTime()));
                    ((TextView) findViewById(R.id.goods_detail_qrcode_text)).setText(goods.getQrcode());
                });
            } catch (Exception e) {
                Log.e("HttpUtil", "根据二维码获取商品信息失败", e);
            }
        }).start();
    }

}