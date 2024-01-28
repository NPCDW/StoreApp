package com.github.npcdw.store

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.github.npcdw.store.config.GlobalConfig
import com.github.npcdw.store.entity.Goods
import com.github.npcdw.store.util.HttpUtil
import com.github.npcdw.store.util.JsonUtil
import com.github.npcdw.store.util.MinioUtil
import com.google.android.material.snackbar.Snackbar
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.math.BigDecimal

class GoodsAddActivity : AppCompatActivity() {
    private var objectName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_add)
        val intent = intent
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val cover = intent.getStringExtra("cover")
        val price = intent.getStringExtra("price")
        val qrcode = intent.getStringExtra("qrcode")
        val unit = intent.getStringExtra("unit")
        val goodsAddGoodsName = findViewById<EditText>(R.id.goods_add_goods_name)
        goodsAddGoodsName.setText(name)
        val goodsAddPrice = findViewById<EditText>(R.id.goods_add_price)
        goodsAddPrice.setText(price)
        val goodsAddQrcodeText = findViewById<TextView>(R.id.goods_add_qrcode_text)
        goodsAddQrcodeText.text = qrcode
        val goodsAddUnit = findViewById<TextView>(R.id.goods_add_unit)
        goodsAddUnit.text = unit
        val imageView = findViewById<ImageView>(R.id.goods_add_image)
        if (StringUtils.isNotBlank(cover)) {
            imageView.background = null
            Glide.with(this).load(GlobalConfig.MINIO_API_BASE_URL + cover).into(imageView)
        }
        val outputImage = File(externalCacheDir, "output_image.jpg")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        val imageUri = FileProvider.getUriForFile(
            this@GoodsAddActivity,
            "com.github.npcdw.store.fileProvider",
            outputImage
        )
        val captureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != RESULT_OK) {
                    return@registerForActivityResult
                }
                imageView.setImageURI(imageUri)
//                val scope = CoroutineScope(Job())
                lifecycleScope.launch {
                    try {
                        Log.i("Image.Size：", (outputImage.length() / 1024).toString() + "KB")
                        val compressedImageFile: File =
                            Compressor.compress(this@GoodsAddActivity, outputImage)
                        Log.i(
                            "Image.Size.Compressed：",
                            (compressedImageFile.length() / 1024).toString() + "KB"
                        )
                        objectName = MinioUtil.upload(
                            GlobalConfig.MINIO_BUCKET,
                            compressedImageFile.absolutePath
                        )
                        if (StringUtils.isNotBlank(id)) {
                            val goods = Goods()
                            goods.id = id!!.toInt()
                            goods.cover = "/" + GlobalConfig.MINIO_BUCKET + "/" + objectName
                            val url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/update"
                            val response = HttpUtil.put(url, JsonUtil.toJsonString(goods)!!)
                            if (StringUtils.isBlank(response)) {
                                Snackbar.make(imageView, "服务器连接失败", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                                return@launch
                            }
                            val map = JsonUtil.parseObject(response!!)
                            if (!(map["success"] as Boolean)) {
                                Snackbar.make(imageView, "上传失败", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                            } else {
                                Snackbar.make(imageView, "上传成功", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Image", "图片压缩上传", e)
                    }
                }
            }

//        ActivityResultLauncher<Intent> pickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                Glide.with(this).load(result.getData().getData()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
//            }
//        });
        imageView.setOnClickListener {
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            captureLauncher.launch(captureIntent)
        }
        val submit = findViewById<Button>(R.id.goods_add_submit)
        submit.setOnClickListener { view: View? ->
            lifecycleScope.launch {
                try {
                    val goods = Goods()
                    goods.name = goodsAddGoodsName.text.toString()
                    goods.price = BigDecimal(goodsAddPrice.text.toString())
                    goods.qrcode = goodsAddQrcodeText.text.toString()
                    goods.unit = goodsAddUnit.text.toString()
                    if (objectName != null) {
                        goods.cover = "/" + GlobalConfig.MINIO_BUCKET + "/" + objectName
                    }
                    val response: String?
                    if (StringUtils.isBlank(id)) {
                        val url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/create"
                        response = HttpUtil.post(url, JsonUtil.toJsonString(goods)!!)
                    } else {
                        goods.id = id!!.toInt()
                        val url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/update"
                        response = HttpUtil.put(url, JsonUtil.toJsonString(goods)!!)
                    }
                    if (StringUtils.isBlank(response)) {
                        Snackbar.make(view!!, "服务器连接失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                        return@launch
                    }
                    val map = JsonUtil.parseObject(response!!)
                    if (!(map["success"] as Boolean)) {
                        if (StringUtils.isBlank(id)) {
                            Snackbar.make(view!!, "创建失败", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        } else {
                            Snackbar.make(view!!, "更新失败", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                    } else {
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("HttpUtil", "更新商品信息失败", e)
                }
            }
        }
    }
}