package com.example.news.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.news.R;
import com.example.news.activity.NewsDetailsActivity;
import com.example.news.data.model.news.Article;
import com.example.news.helper.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsHomeAdapter extends RecyclerView.Adapter<NewsHomeAdapter.MyViewHolder> {

    private Context context;
    private List<Article> articles = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public NewsHomeAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_news, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        Article model = articles.get(i);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        myViewHolder.itemHomeNewsProgressBarLoadPhoto.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        myViewHolder.itemHomeNewsProgressBarLoadPhoto.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(myViewHolder.itemHomeNewsImageViewImage);

        myViewHolder.itemHomeNewsTextViewTitle.setText(model.getTitle());
        myViewHolder.itemHomeNewsTextViewDescription.setText(model.getDescription());
        myViewHolder.itemHomeNewsTextViewSource.setText(model.getSource().getName());
        myViewHolder.itemHomeNewsTextViewTime.setText(" \u2022 " + Utils.DateToTimeFormat(model.getPublishedAt()));
        myViewHolder.itemHomeNewsTextViewPublishAt.setText(Utils.DateFormat(model.getPublishedAt()));
        myViewHolder.itemHomeNewsTextViewAuthor.setText(model.getAuthor());
        myViewHolder.itemHomeNewsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetailsActivity.class);
                Article article = articles.get(i);
                intent.putExtra("url",article.getUrl());
                intent.putExtra("title",article.getTitle());
                intent.putExtra("image",article.getUrlToImage());
                intent.putExtra("date",article.getPublishedAt());
                intent.putExtra("source",article.getSource().getName());
                intent.putExtra("author",article.getAuthor());
                Pair<View , String> pair = Pair.create((View)myViewHolder.itemHomeNewsImageViewImage, ViewCompat.getTransitionName(myViewHolder.itemHomeNewsImageViewImage));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,pair);

                context.startActivity(intent,optionsCompat.toBundle());

            }
        });

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int Position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_Home_news_ImageView_Image)
        ImageView itemHomeNewsImageViewImage;
        @BindView(R.id.item_Home_news_ImageView_Shadow_Bottom)
        ImageView itemHomeNewsImageViewShadowBottom;
        @BindView(R.id.item_Home_news_ProgressBar_Load_Photo)
        ProgressBar itemHomeNewsProgressBarLoadPhoto;
        @BindView(R.id.item_Home_news_TextView_Author)
        TextView itemHomeNewsTextViewAuthor;
        @BindView(R.id.item_Home_news_TextView_PublishAt)
        TextView itemHomeNewsTextViewPublishAt;
        @BindView(R.id.item_Home_news_TextView_Title)
        TextView itemHomeNewsTextViewTitle;
        @BindView(R.id.item_Home_news_TextView_Description)
        TextView itemHomeNewsTextViewDescription;
        @BindView(R.id.item_Home_news_TextView_Source)
        TextView itemHomeNewsTextViewSource;
        @BindView(R.id.item_Home_news_TextView_Time)
        TextView itemHomeNewsTextViewTime;
        @BindView(R.id.item_Home_news_CardView)
        CardView itemHomeNewsCardView;
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
            View view = itemView;
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
