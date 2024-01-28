package com.github.npcdw.store

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.npcdw.store.config.GlobalConfig
import com.github.npcdw.store.entity.Goods
import com.github.npcdw.store.util.HttpUtil
import com.github.npcdw.store.util.JsonUtil
import com.google.android.material.snackbar.Snackbar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils

class GoodsListActivity : AppCompatActivity() {
    private lateinit var refreshLayout: RefreshLayout
    private var list: MutableList<Goods> = ArrayList()
    private var count = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var typeEnum: TypeEnum
    private var pageNumber = 1
    private val pageSize = 10
    private var search = ""
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_goods_list_menu, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //单机搜索按钮时激发该方法
            override fun onQueryTextSubmit(query: String): Boolean {
                typeEnum = TypeEnum.REFRESH
                search = query
                getList()
                return false
            }

            //用户输入字符时激发该方法
            override fun onQueryTextChange(newText: String): Boolean {
                if (searchView.query.isEmpty()) {
                    typeEnum = TypeEnum.REFRESH
                    search = ""
                    getList()
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_list)
        recyclerView = findViewById(R.id.goods_list_recyclerview)
        recyclerView.setAdapter(GoodsListAdapter())
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        recyclerView.setHasFixedSize(true)
        refreshLayout = findViewById(R.id.goods_list_refresh_layout)
        refreshLayout.setOnRefreshListener {
            typeEnum = TypeEnum.REFRESH
            getList()
        }
        refreshLayout.setOnLoadMoreListener {
            typeEnum = TypeEnum.LOAD_MORE
            getList()
        }
        typeEnum = TypeEnum.REFRESH
        getList()
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                200 -> recyclerView.adapter = GoodsListAdapter()
                -1 -> Snackbar.make(recyclerView, "服务器连接失败", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

                -2 -> Snackbar.make(recyclerView, "获取失败", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            if (count == list.size) {
                refreshLayout.finishLoadMoreWithNoMoreData()
            }
            if (typeEnum == TypeEnum.LOAD_MORE) {
                refreshLayout.finishLoadMore()
            } else {
                refreshLayout.finishRefresh()
            }
        }
    }

    fun getList() {
        if (typeEnum == TypeEnum.LOAD_MORE) {
            pageNumber++
        } else {
            pageNumber = 1
        }
        lifecycleScope.launch {
            try {
                val url =
                    GlobalConfig.INTERFACE_API_BASE_URL + "/goods/list?pageNumber=" + pageNumber + "&pageSize=" + pageSize + "&name=" + search
                val response = HttpUtil.get(url)
                if (response == null) {
                    val message = Message()
                    message.what = -1
                    handler.sendMessage(message)
                    return@launch
                }
                val map = JsonUtil.parseObject(response)
                if (!(map["success"] as Boolean)) {
                    val message = Message()
                    message.what = -2
                    handler.sendMessage(message)
                    return@launch
                }
                val dataMap = JsonUtil.parseObject(JsonUtil.toJsonString(map["data"])!!)
                count = dataMap["count"].toString().toInt()
                if (typeEnum == TypeEnum.LOAD_MORE) {
                    list.addAll(
                        JsonUtil.parseArray(
                            JsonUtil.toJsonString(dataMap["list"])!!,
                            Goods::class.java
                        )
                    )
                } else {
                    list = JsonUtil.parseArray(
                        JsonUtil.toJsonString(dataMap["list"])!!,
                        Goods::class.java
                    ).toMutableList()
                }
                val message = Message()
                message.what = 200
                handler.sendMessage(message)
            } catch (e: Exception) {
                Log.e("HttpUtil", "搜索获取商品列表失败", e)
                val message = Message()
                message.what = -2
                handler.sendMessage(message)
            }
        }
    }

    internal enum class TypeEnum {
        REFRESH,
        LOAD_MORE
    }

    internal inner class GoodsListAdapter : RecyclerView.Adapter<GoodsListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsListViewHolder {
            val view = LayoutInflater.from(this@GoodsListActivity)
                .inflate(R.layout.activity_goods_list_item, parent, false)
            return GoodsListViewHolder(view)
        }

        override fun onBindViewHolder(holder: GoodsListViewHolder, position: Int) {
            val goods = list[position]
            if (StringUtils.isNotBlank(goods.cover)) {
                Glide.with(this@GoodsListActivity)
                    .load(GlobalConfig.MINIO_API_BASE_URL + goods.cover).into(holder.cover)
            }
            holder.name.text = goods.name
            holder.price.text = if (goods.price == null) "" else "￥" + goods.price.toString()
            holder.itemView.setOnClickListener {
                val intent = Intent(this@GoodsListActivity, GoodsDetailActivity::class.java)
                intent.putExtra("id", goods.id)
                startActivity(intent)
            }
            holder.delete.setOnClickListener {
                lifecycleScope.launch() {
                    try {
                        val url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/remove/" + goods.id
                        val response = HttpUtil.delete(url)
                        if (response == null) {
                            val message = Message()
                            message.what = -1
                            handler.sendMessage(message)
                            return@launch
                        }
                        val map = JsonUtil.parseObject(response)
                        if (!(map["success"] as Boolean)) {
                            val message = Message()
                            message.what = -2
                            handler.sendMessage(message)
                            return@launch
                        }
                        list.removeAt(position)
                        val message = Message()
                        message.what = 200
                        handler.sendMessage(message)
                    } catch (e: Exception) {
                        Log.e("HttpUtil", "根据二维码获取商品信息失败", e)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    internal class GoodsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cover: ImageView
        var name: TextView
        var price: TextView
        var delete: TextView

        init {
            cover = view.findViewById(R.id.goods_list_image)
            name = view.findViewById(R.id.goods_list_goods_name)
            price = view.findViewById(R.id.goods_list_goods_price)
            delete = view.findViewById(R.id.goods_list_goods_delete)
        }
    }
}