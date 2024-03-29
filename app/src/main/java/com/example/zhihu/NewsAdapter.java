package com.example.zhihu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView的适配器
 */
public class NewsAdapter extends RecyclerView.Adapter implements OnBannerListener {

    private final static int ITEM_NEWS = 0;
    private final static int ITEM_BANNER = 1;
    //    private final static int ITEM_LINE = 2;
    private List<News> mNewsList;
    private List<TopNews> mTopNewsList;
//    private List<String> mTimeLineList;
    private Context context;

    static class NewsHolder extends RecyclerView.ViewHolder {
        View newsView;
        final TextView textTitle;
        final TextView textHint;
        final ImageView newsImage;

        NewsHolder(@NonNull final View view) {
            super(view);
            newsView = view;
            textTitle = view.findViewById(R.id.news_title);
            textHint = view.findViewById(R.id.news_hint);
            newsImage = view.findViewById(R.id.news_image);
        }
    }

    static class BannerHolder extends RecyclerView.ViewHolder {
        final Banner banner;

        BannerHolder(@NonNull final View view) {
            super(view);
            banner = view.findViewById(R.id.top_banner);
        }
    }

//    static class LineHolder extends RecyclerView.ViewHolder {
//        View lineView;
//        final TextView timeLine;
//
//        LineHolder(@NonNull View view) {
//            super(view);
//            timeLine = itemView.findViewById(R.id.time_line);
//        }
//    }

    public NewsAdapter(List<News> newsList, List<TopNews> topNews,Context context) {
        mNewsList = newsList;
        mTopNewsList = topNews;
//        mTimeLineList = timeLineList;
        this.context=context;
    }


    //创建viewHolder引入xml
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_NEWS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            final NewsHolder holder = new NewsHolder(view);
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_item, parent, false);
            final BannerHolder holder = new BannerHolder(view);
            initView(holder.banner);
            return holder;
        }
//        } else {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dividing_line, parent, false);
//            final LineHolder holder = new LineHolder(view);
//            return holder;
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NewsHolder) {
            final News news = mNewsList.get(position - 1);
            NewsHolder newsHolder = (NewsHolder) holder;
            newsHolder.textTitle.setText(news.getTitle());
            newsHolder.textHint.setText(news.getHint());
            Glide.with(context).load(news.getImage()).into(newsHolder.newsImage);
            newsHolder.newsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.actionStart(context, news.getUrl());
                }
            });
        }
//        if (holder instanceof BannerHolder) {
//            final BannerHolder bannerHolder = (BannerHolder) holder;
//            bannerHolder.bannerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    WebViewActivity.actionStart(MainActivity.getContext(),mTopNewsList.get(position-1).getUrl());
//                }
//            });
//        }
//        if (holder instanceof LineHolder) {
//        }
    }

    @Override
    public int getItemCount() {
        return mNewsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return ITEM_BANNER;
        else return ITEM_NEWS;
    }

    private void initView(Banner banner) {
        ArrayList<String> list_path = new ArrayList<>();
        ArrayList<String> list_title = new ArrayList<>();
        for (int i = 0; i < mTopNewsList.size(); i++) {
            list_path.add(mTopNewsList.get(i).getImage());
            list_title.add(mTopNewsList.get(i).getTitle());

        }
        //设置Banner
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);  //设置样式
        banner.setImageLoader(new MyLoader());  //设置图片加载器
        banner.setImages(list_path);  //设置图片网址或地址的集合
        banner.setBannerTitles(list_title);  //设置轮播图的标题集合
        banner.setBannerAnimation(Transformer.DepthPage);  //设置动画效果
        banner.isAutoPlay(true);  //设置是否自动轮播
        banner.setDelayTime(4000);  //设置自动轮播时间
        banner.setIndicatorGravity(BannerConfig.RIGHT)  //设置指示器的位置
                .setOnBannerListener(this)
                .start();
    }

    //Banner的监听效果
    @Override
    public void OnBannerClick(int position) {
        WebViewActivity.actionStart(context, mTopNewsList.get(position).getUrl());
    }

    //自定义的图片加载器
    public static class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((String) path).into(imageView);
        }
    }

//    public void getTimeLine(Bean bean){
//        mTimeLineList.add(bean.getDate());
//    }
}