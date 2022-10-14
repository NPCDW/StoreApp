package com.github.npcdw.store;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.github.npcdw.store.entity.Goods;
import com.github.npcdw.store.config.GlobalConfig;
import com.github.npcdw.store.util.HttpUtil;
import com.github.npcdw.store.util.JsonUtil;
import com.github.npcdw.store.util.MinioUtil;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class GoodsAddActivity extends AppCompatActivity {
    private String objectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_add);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String cover = intent.getStringExtra("cover");
        String price = intent.getStringExtra("price");
        String qrcode = intent.getStringExtra("qrcode");
        String unit = intent.getStringExtra("unit");

        EditText goods_add_goods_name = findViewById(R.id.goods_add_goods_name);
        goods_add_goods_name.setText(name);
        EditText goods_add_price = findViewById(R.id.goods_add_price);
        goods_add_price.setText(price);
        TextView goods_add_qrcode_text = findViewById(R.id.goods_add_qrcode_text);
        goods_add_qrcode_text.setText(qrcode);
        TextView goods_add_unit = findViewById(R.id.goods_add_unit);
        goods_add_unit.setText(unit);
        ImageView imageView = findViewById(R.id.goods_add_image);
        if (StringUtils.isNotBlank(cover)) {
            imageView.setBackground(null);
            Glide.with(this).load(GlobalConfig.MINIO_API_BASE_URL + cover).into(imageView);
        }

        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        if (outputImage.exists()) {
            boolean delete = outputImage.delete();
        }
        Uri imageUri = FileProvider.getUriForFile(GoodsAddActivity.this, "com.github.npcdw.store.fileProvider", outputImage);

        ActivityResultLauncher<Intent> captureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() != RESULT_OK) {
                return;
            }
            imageView.setImageURI(imageUri);

            new Thread(() -> {
                try {
                    Log.i("Image.Size：", outputImage.length() / 1024 + "KB");
                    File compressedImageFile = new Compressor(this).compressToFile(outputImage);
                    Log.i("Image.Size.Compressed：", compressedImageFile.length() / 1024 + "KB");
                    objectName = MinioUtil.upload(GlobalConfig.MINIO_BUCKET, compressedImageFile.getAbsolutePath());
                    if (StringUtils.isNotBlank(id)) {
                        Goods goods = new Goods();
                        goods.setId(Integer.parseInt(id));
                        goods.setCover("/" + GlobalConfig.MINIO_BUCKET + "/" + objectName);
                        String url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/update";
                        String response = HttpUtil.put(url, JsonUtil.toJsonString(goods));
                        if (StringUtils.isBlank(response)) {
                            Snackbar.make(imageView, "服务器连接失败", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            return;
                        }
                        Map<String, Object> map = JsonUtil.parseObject(response);
                        if (map == null || !Boolean.parseBoolean(map.get("success").toString())) {
                            Snackbar.make(imageView, "上传失败", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            Snackbar.make(imageView, "上传成功", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("Image", "图片压缩上传", e);
                }
            }).start();
        });

//        ActivityResultLauncher<Intent> pickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                Glide.with(this).load(result.getData().getData()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
//            }
//        });

        imageView.setOnClickListener(view -> {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            captureLauncher.launch(captureIntent);

//            Intent pickIntent = new Intent(Intent.ACTION_PICK);
//            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//            pickLauncher.launch(pickIntent);
        });

        Button submit = findViewById(R.id.goods_add_submit);
        submit.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    Goods goods = new Goods();
                    goods.setName(goods_add_goods_name.getText().toString());
                    goods.setPrice(new BigDecimal(goods_add_price.getText().toString()));
                    goods.setQrcode(goods_add_qrcode_text.getText().toString());
                    goods.setUnit(goods_add_unit.getText().toString());
                    if (objectName != null) {
                        goods.setCover("/" + GlobalConfig.MINIO_BUCKET + "/" + objectName);
                    }
                    String response;
                    if (StringUtils.isBlank(id)) {
                        String url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/create";
                        response = HttpUtil.post(url, JsonUtil.toJsonString(goods));
                    } else {
                        goods.setId(Integer.parseInt(id));
                        String url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/update";
                        response = HttpUtil.put(url, JsonUtil.toJsonString(goods));
                    }
                    if (StringUtils.isBlank(response)) {
                        Snackbar.make(view, "服务器连接失败", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }
                    Map<String, Object> map = JsonUtil.parseObject(response);
                    if (map == null || !Boolean.parseBoolean(map.get("success").toString())) {
                        if (StringUtils.isBlank(id)) {
                            Snackbar.make(view, "创建失败", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            Snackbar.make(view, "更新失败", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        GoodsAddActivity.this.finish();
                    }
                } catch (Exception e) {
                    Log.e("HttpUtil", "更新商品信息失败", e);
                }
            }).start();
        });
    }

}