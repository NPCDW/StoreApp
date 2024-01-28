package com.github.npcdw.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.github.npcdw.store.config.GlobalConfig
import com.github.npcdw.store.databinding.ActivityGoodsDetailBinding
import com.github.npcdw.store.entity.Goods
import com.github.npcdw.store.util.DateTimeUtil
import com.github.npcdw.store.util.HttpUtil
import com.github.npcdw.store.util.JsonUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils

class GoodsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsDetailBinding
    private var qrcode: String? = null
    private var id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        qrcode = intent.getStringExtra("qrcode")
        id = intent.getIntExtra("id", 0)
    }

    override fun onResume() {
        super.onResume()
        val fab: FloatingActionButton = binding.fab
        lifecycleScope.launch {
            try {
                val url: String
                url = if (id != 0) {
                    GlobalConfig.INTERFACE_API_BASE_URL + "/goods/getInfo/" + id
                } else {
                    GlobalConfig.INTERFACE_API_BASE_URL + "/goods/getInfoByQRCode?qrcode=" + qrcode
                }
                val response = HttpUtil.get(url)
                if (response == null) {
                    Snackbar.make(binding.getRoot(), "服务器连接失败", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                    return@launch
                }
                val map = JsonUtil.parseObject(response)
                if (!(map["success"] as Boolean)) {
                    Snackbar.make(binding.getRoot(), "获取失败", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                    return@launch
                }
                this@GoodsDetailActivity.runOnUiThread {
                    fab.visibility = View.VISIBLE
                    if (map["data"] == null) {
                        findViewById<View>(R.id.goods_detail_goods_no_exist_layout).setVisibility(
                            View.VISIBLE
                        )
                        fab.setImageResource(R.drawable.ic_baseline_add_24)
                        fab.setOnClickListener {
                            val intent2 = Intent(this@GoodsDetailActivity, GoodsAddActivity::class.java)
                            intent2.putExtra("qrcode", qrcode)
                            startActivity(intent2)
                        }
                        return@runOnUiThread
                    }
                    var goods: Goods? = null
                    try {
                        goods = JsonUtil.parseObject<Goods>(
                            JsonUtil.toJsonString(map["data"])!!,
                            Goods::class.java
                        )
                    } catch (e: Exception) {
                        Log.e("JsonUtil", "Jackson序列化失败", e)
                    }
                    if (goods == null) {
                        return@runOnUiThread
                    }
                    findViewById<View>(R.id.goods_detail_goods_exist_layout).setVisibility(View.VISIBLE)
                    fab.setImageResource(R.drawable.ic_baseline_edit_24)
                    val finalGoods: Goods = goods
                    fab.setOnClickListener {
                        val intent2: Intent =
                            Intent(this@GoodsDetailActivity, GoodsAddActivity::class.java)
                        intent2.putExtra("id", finalGoods.id.toString())
                        intent2.putExtra("name", finalGoods.name)
                        intent2.putExtra("cover", finalGoods.cover)
                        intent2.putExtra("price", finalGoods.price.toString())
                        intent2.putExtra("unit", finalGoods.unit)
                        intent2.putExtra("qrcode", finalGoods.qrcode)
                        startActivity(intent2)
                    }
                    if (StringUtils.isNotBlank(goods.cover)) {
                        val imageView: ImageView = findViewById(R.id.goods_detail_cover)
                        imageView.background = null
                        Glide.with(this@GoodsDetailActivity).load(GlobalConfig.MINIO_API_BASE_URL + goods.cover)
                            .into(imageView)
                    }
                    (findViewById<View>(R.id.goods_detail_goods_name) as TextView).setText(goods.name)
                    (findViewById<View>(R.id.goods_detail_goods_price) as TextView).text =
                        if (goods.price == null) "" else "￥" + goods.price.toString()
                    (findViewById<View>(R.id.goods_detail_unit) as TextView).setText(goods.unit)
                    (findViewById<View>(R.id.goods_detail_create_time) as TextView).setText(
                        DateTimeUtil.formatDateTime(goods.createTime!!)
                    )
                    (findViewById<View>(R.id.goods_detail_update_time) as TextView).setText(
                        DateTimeUtil.formatDateTime(goods.updateTime!!)
                    )
                    (findViewById<View>(R.id.goods_detail_qrcode_text) as TextView).setText(goods.qrcode)
                }
            } catch (e: Exception) {
                Log.e("HttpUtil", "根据二维码获取商品信息失败", e)
            }
        }
    }
}