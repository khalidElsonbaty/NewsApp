package com.example.news.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.adapter.NewsHomeAdapter;
import com.example.news.data.api.ApiClient;
import com.example.news.data.api.ApiServices;
import com.example.news.data.model.news.Article;
import com.example.news.data.model.news.News;
import com.example.news.helper.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String API_KEY = "24348935503a485085aa857f32c7ca9a";
    @BindView(R.id.Error_ImageView)
    ImageView ErrorImageView;
    @BindView(R.id.Error_TextView_Title)
    TextView ErrorTextViewTitle;
    @BindView(R.id.Error_TextView_Message)
    TextView ErrorTextViewMessage;
    @BindView(R.id.Error_Layout)
    RelativeLayout ErrorLayout;
    @BindView(R.id.Error_Button_Retry)
    Button ErrorButtonRetry;
    private RecyclerView myRecyclerView;
    /*private RecyclerView.LayoutManager layoutManager;*/
    private LinearLayoutManager linearLayoutManager;
    private List<Article> articles = new ArrayList<>();
    private NewsHomeAdapter myRecyclerAdapter;
    private String TAG = HomeActivity.class.getSimpleName();
    ApiServices apiServices;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView topHeadLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.Home_ToolBar);
        setSupportActionBar(toolbar);
        topHeadLine = (TextView) findViewById(R.id.Home_TextView_TopHeadLine);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.Home_SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        myRecyclerView = (RecyclerView) findViewById(R.id.Home_RecyclerView_News);
        linearLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        /*layoutManager = new LinearLayoutManager(HomeActivity.this);*/
        myRecyclerAdapter = new NewsHomeAdapter(this, articles);
        /*myRecyclerView.setAdapter(myRmyRecyclerAdapter);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());*/
        myRecyclerView.setNestedScrollingEnabled(false);
        myRecyclerView.setAdapter(myRecyclerAdapter);

        onLoadingSwipeRefresh();
    }

    public void loadData() {
        ErrorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        String country = Utils.getCountry();
        apiServices = ApiClient.getApiClient().create(ApiServices.class);
        apiServices.getNews(country, API_KEY).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                Log.i(TAG, "onNewsResponse: " + response.raw());
                if (response.isSuccessful() && response.body().getArticle() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();
                    }
                    articles.addAll(response.body().getArticle());
                    myRecyclerAdapter.notifyDataSetChanged();
                    //initListener();
                    topHeadLine.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    topHeadLine.setVisibility(View.INVISIBLE);

                    String errorCode;
                    switch (response.code()){
                        case 404:
                            errorCode= "404 Not Found";
                            break;
                        case 500:
                            errorCode = "500 Server Broken";
                            break;
                            default:
                                errorCode = "An Error Found";
                                break;
                    }
                    showErrorMessage(R.drawable.no_result,
                            "No Result",
                            "Please Try Again!"+"\n"+errorCode);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                topHeadLine.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                showErrorMessage(R.drawable.no_result,
                        "Oops...",
                        "Network Failure, Please Try Again!"+"\n"+ t.toString());
            }
        });
    }

    private void initListener() {
        NewsHomeAdapter newsHomeAdapter = new NewsHomeAdapter(this, articles);
        newsHomeAdapter.setOnItemClickListener(new NewsHomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(HomeActivity.this, NewsDetailsActivity.class);
                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("image", article.getUrlToImage());
                intent.putExtra("date", article.getPublishedAt());
                intent.putExtra("source", article.getSource().getName());
                intent.putExtra("author", article.getAuthor());

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.Home_News_Menu_Search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.Home_News_Menu_Search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    loadSearchData(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadSearchData(newText);
                return false;
            }
        });
        searchMenuItem.getIcon().setVisible(false, false);
        return true;
    }

    private void loadSearchData(String keyword) {
        ErrorLayout.setVisibility(View.GONE);
        String language = Utils.getLanguage();
        apiServices = ApiClient.getApiClient().create(ApiServices.class);
        apiServices.getNewsSearch(keyword, language, "publishedAt", API_KEY).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                swipeRefreshLayout.setRefreshing(true);
                if (response.isSuccessful() && response.body().getArticle() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();
                    }
                    articles.addAll(response.body().getArticle());
                    myRecyclerAdapter.notifyDataSetChanged();
                    //initListener();
                    topHeadLine.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    topHeadLine.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    String errorCode;
                    switch (response.code()){
                        case 404:
                            errorCode= "404 Not Found";
                            break;
                        case 500:
                            errorCode = "500 Server Broken";
                            break;
                        default:
                            errorCode = "An Error Found";
                            break;
                    }
                    showErrorMessage(R.drawable.no_result,
                            "No Result",
                            "Please Try Again!"+"\n"+errorCode);                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                topHeadLine.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                showErrorMessage(R.drawable.no_result,
                        "Oops...",
                        "Network Failure, Please Try Again!"+"\n"+ t.toString());
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    public void onLoadingSwipeRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
    }

    private void showErrorMessage(int imageView, String title, String message) {
        if (ErrorLayout.getVisibility() == View.GONE) {
            ErrorLayout.setVisibility(View.VISIBLE);
        }
        ErrorImageView.setImageResource(imageView);
        ErrorTextViewTitle.setText(title);
        ErrorTextViewMessage.setText(message);
        ErrorButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadingSwipeRefresh();
            }
        });
    }
}
