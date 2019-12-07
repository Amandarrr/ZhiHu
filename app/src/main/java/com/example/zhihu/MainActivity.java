package com.example.zhihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private List<News> newsList = new ArrayList<>();
    private List<TopNews> topNewsList = new ArrayList<>();
    private String date;
    private NewsAdapter newsAdapter;
    private RecyclerView recyclerView;
    private static boolean isComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backTop();
        getTime();
        load(true);
        refreshList();
    }

    //上拉加载
    private void initAdapter() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsList, topNewsList,MainActivity.this);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalItemCount;
            private int firstVisibleItem;
            private int visibleItemCount;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                visibleItemCount = recyclerView.getChildCount();
                if ( ((totalItemCount - visibleItemCount) <= firstVisibleItem) && isComplete) {
                    load(false);
                }
            }
        });
    }

    private void load(boolean isToday) {
        if (isToday) {
            isComplete=false;
            sendRequest("https://news-at.zhihu.com/api/3/news/latest", true);
        } else {
            isComplete=false;
            sendRequest("http://news-at.zhihu.com/api/4/news/before/" + date, false);
        }
    }

    private void sendRequest(String url, final boolean isToday) {
        Client.sendRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call,@NonNull IOException e) {
                Log.d("Failure", e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call,@NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String result = response.body().string();
                    Message message = Message.obtain();
                    if (isToday) {
                        message.what = 1;
                    } else {
                        message.what = 0;
                    }
                    message.obj = result;
                    handler.sendMessage(message);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Gson gson = new Gson();
            Bean bean = gson.fromJson(msg.obj.toString(), Bean.class);
            date=bean.getDate();
            switch (msg.what) {
                case 1:
                    addTopNews(bean);
                    addNews(bean);
                    initAdapter();
                    break;
                case 0:
                    addNews(bean);
                    break;
            }
            newsAdapter.notifyDataSetChanged();
            isComplete=true;
        }
    };

    private void addNews(Bean bean) {
        String title, hint, image, url;
        for (int i = 0; i < bean.getStories().size(); i++) {
            title = bean.getStories().get(i).getTitle();
            hint = bean.getStories().get(i).getHint();
            image = bean.getStories().get(i).getImages().get(0);
            url = bean.getStories().get(i).getUrl();
            newsList.add(new News(title, hint, image, url));
        }
    }

    private void addTopNews(Bean bean) {
        String title, hint, image, url;
        for (int i = 0; i < bean.getTop_stories().size(); i++) {
            title = bean.getTop_stories().get(i).getTitle();
            hint = bean.getTop_stories().get(i).getHint();
            image = bean.getTop_stories().get(i).getImage();
            url = bean.getTop_stories().get(i).getUrl();
            topNewsList.add(new TopNews(title, hint, image, url));
        }
    }

    //下拉刷新
    private void refreshList() {
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsList.clear();
                topNewsList.clear();
                load(true);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    //设置Banner
    private void backTop(){
        Toolbar mToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });
    }

    //设置欢迎语句
    private void getTime() {
        TextView viewDay = findViewById(R.id.time_day);
        TextView viewMonth = findViewById(R.id.time_month);
        TextView viewGreeting = findViewById(R.id.title_greeting);
        Calendar c = Calendar.getInstance();
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH) + 1;
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        viewDay.setText(day + "");
        viewMonth.setText(month + "月");
        if (hour > 5 && hour <= 9) {
            viewGreeting.setText("早上好！^_^");
        } else if (hour > 9 && hour <= 14) {
            viewGreeting.setText("中午好！^_^");
        } else if (hour > 14 && hour <= 18) {
            viewGreeting.setText("下午好！^_^");
        } else if (hour > 18 && hour <= 23) {
            viewGreeting.setText("晚上好！^_^");
        } else viewGreeting.setText("睡觉时间到！");
    }
}

