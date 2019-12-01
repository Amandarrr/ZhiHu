package com.example.zhihu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
    private List<String> dateList = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        Toolbar mToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        getTime();
        load(true);
        refreshList();
    }

    private void initAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsList, topNewsList);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                load(false);
            }
        });
    }

    private void load(boolean isToday) {
        if (isToday) {
            sendRequest("https://news-at.zhihu.com/api/3/news/latest", true);
        } else {
            sendRequest("https://news-at.zhihu.com/api/3/news/before/" + dateList.get(dateList.size() - 1), false);
            Toast.makeText(MainActivity.this, dateList.get(dateList.size() - 1), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequest(String url, final boolean isToday) {
        Client.sendRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Failure", e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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

    //获得当前的date用来得到url
    private void addDateList(Bean bean) {
        dateList.add(bean.getDate());
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Gson gson = new Gson();
            Bean bean = gson.fromJson(msg.obj.toString(), Bean.class);
            switch (msg.what) {
                case 1:
                    addTopNews(bean);
                    addNews(bean);
                    addDateList(bean);
                    initAdapter();
                    break;
                case 0:
                    addNews(bean);
                    addDateList(bean);
                    break;
            }
            newsAdapter.notifyDataSetChanged();
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
                dateList.clear();
                newsList.clear();
                topNewsList.clear();
                load(true);
                refreshLayout.setRefreshing(false);
            }
        });
    }

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

    public static Context getContext() {
        return context;
    }
}

