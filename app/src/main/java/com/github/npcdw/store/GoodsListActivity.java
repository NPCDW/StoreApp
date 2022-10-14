package com.github.npcdw.store;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.npcdw.store.entity.Goods;
import com.github.npcdw.store.config.GlobalConfig;
import com.github.npcdw.store.util.HttpUtil;
import com.github.npcdw.store.util.JsonUtil;
import com.google.android.material.snackbar.Snackbar;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoodsListActivity extends AppCompatActivity {
    private RefreshLayout refreshLayout;
    private List<Goods> list = new ArrayList<>();
    private int count;
    private RecyclerView recyclerView;
    private TypeEnum typeEnum;
    private int pageNumber = 1;
    private final int pageSize = 10;
    private String search = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_goods_list_menu, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //单机搜索按钮时激发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                typeEnum = TypeEnum.REFRESH;
                search = query;
                getList();
                return false;
            }

            //用户输入字符时激发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getQuery().length() == 0) {
                    typeEnum = TypeEnum.REFRESH;
                    search = "";
                    getList();
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);

        recyclerView = findViewById(R.id.goods_list_recyclerview);
        recyclerView.setAdapter(new GoodsListAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        refreshLayout = findViewById(R.id.goods_list_refresh_layout);
        refreshLayout.setOnRefreshListener(layout -> {
            typeEnum = TypeEnum.REFRESH;
            getList();
        });
        refreshLayout.setOnLoadMoreListener(layout -> {
            typeEnum = TypeEnum.LOAD_MORE;
            getList();
        });

        typeEnum = TypeEnum.REFRESH;
        getList();
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    recyclerView.setAdapter(new GoodsListAdapter());
                    break;
                case -1:
                    Snackbar.make(GoodsListActivity.this.recyclerView, "服务器连接失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                case -2:
                    Snackbar.make(GoodsListActivity.this.recyclerView, "获取失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
            }
            if (count == list.size()) {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
            if (typeEnum == TypeEnum.LOAD_MORE) {
                refreshLayout.finishLoadMore();
            } else {
                refreshLayout.finishRefresh();
            }
        }
    };

    public void getList() {
        if (typeEnum == TypeEnum.LOAD_MORE) {
            pageNumber++;
        } else {
            pageNumber = 1;
        }
        new Thread(() -> {
            try {
                String url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/list?pageNumber=" + pageNumber + "&pageSize=" + pageSize + "&name=" + search;
                String response = HttpUtil.get(url);
                if (response == null) {
                    Message message = new Message();
                    message.what = -1;
                    handler.sendMessage(message);
                    return;
                }
                Map<String, Object> map = JsonUtil.parseObject(response);
                if (map == null || !Boolean.parseBoolean(map.get("success").toString())) {
                    Message message = new Message();
                    message.what = -2;
                    handler.sendMessage(message);
                    return;
                }
                Map<String, Object> dataMap = JsonUtil.parseObject(JsonUtil.toJsonString(map.get("data")));
                count = Integer.parseInt(dataMap.get("count").toString());
                if (typeEnum == TypeEnum.LOAD_MORE) {
                    list.addAll(JsonUtil.parseArray(JsonUtil.toJsonString(dataMap.get("list")), Goods.class));
                } else {
                    list = JsonUtil.parseArray(JsonUtil.toJsonString(dataMap.get("list")), Goods.class);
                }
                Message message = new Message();
                message.what = 200;
                handler.sendMessage(message);
            } catch (Exception e) {
                Log.e("HttpUtil", "搜索获取商品列表失败", e);
                Message message = new Message();
                message.what = -2;
                handler.sendMessage(message);
            }
        }).start();
    }

    enum TypeEnum {
        REFRESH, LOAD_MORE
    }

    class GoodsListAdapter extends RecyclerView.Adapter<GoodsListViewHolder> {
        @NonNull
        @Override
        public GoodsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(GoodsListActivity.this).inflate(R.layout.activity_goods_list_item, parent, false);
            return new GoodsListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GoodsListViewHolder holder, int position) {
            Goods goods = list.get(position);
            if (StringUtils.isNotBlank(goods.getCover())) {
                Glide.with(GoodsListActivity.this).load(GlobalConfig.MINIO_API_BASE_URL + goods.getCover()).into(holder.cover);
            }
            holder.name.setText(goods.getName());
            holder.price.setText(goods.getPrice() == null ? "" : "￥" + goods.getPrice().toString());
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(GoodsListActivity.this, GoodsDetailActivity.class);
                intent.putExtra("id", goods.getId());
                startActivity(intent);
            });
            holder.delete.setOnClickListener(view -> {
                new Thread(() -> {
                    try {
                        String url = GlobalConfig.INTERFACE_API_BASE_URL + "/goods/remove/" + goods.getId();
                        String response = HttpUtil.delete(url);
                        if (response == null) {
                            Message message = new Message();
                            message.what = -1;
                            handler.sendMessage(message);
                            return;
                        }
                        Map<String, Object> map = JsonUtil.parseObject(response);
                        if (map == null || !Boolean.parseBoolean(map.get("success").toString())) {
                            Message message = new Message();
                            message.what = -2;
                            handler.sendMessage(message);
                            return;
                        }
                        list.remove(position);
                        Message message = new Message();
                        message.what = 200;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        Log.e("HttpUtil", "根据二维码获取商品信息失败", e);
                    }
                }).start();
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    static class GoodsListViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView name;
        TextView price;
        TextView delete;

        public GoodsListViewHolder(@NonNull View view) {
            super(view);
            cover = view.findViewById(R.id.goods_list_image);
            name = view.findViewById(R.id.goods_list_goods_name);
            price = view.findViewById(R.id.goods_list_goods_price);
            delete = view.findViewById(R.id.goods_list_goods_delete);
        }
    }
}