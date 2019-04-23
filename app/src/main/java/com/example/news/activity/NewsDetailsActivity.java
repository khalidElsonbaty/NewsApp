package com.example.news.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.news.R;
import com.example.news.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.News_Details_ImageView_BackDrop)
    ImageView NewsDetailsImageViewBackDrop;
    @BindView(R.id.News_Details_ImageView_TopShadow)
    ImageView NewsDetailsImageViewTopShadow;
    @BindView(R.id.News_Details_ImageView_BottomShadow)
    ImageView NewsDetailsImageViewBottomShadow;
    @BindView(R.id.News_Details_TextView_Title_Appbar)
    TextView NewsDetailsTextViewTitleAppbar;
    @BindView(R.id.News_Details_TextView_Subtitle_Appbar)
    TextView NewsDetailsTextViewSubtitleAppbar;
    @BindView(R.id.News_Details_Title_Appbar)
    LinearLayout NewsDetailsTitleAppbar;
    @BindView(R.id.News_Details_Toolbar)
    Toolbar NewsDetailsToolbar;
    @BindView(R.id.News_Details_AppBarLayout)
    AppBarLayout NewsDetailsAppBarLayout;
    @BindView(R.id.News_Details_TextView_Title)
    TextView NewsDetailsTextViewTitle;
    @BindView(R.id.News_Details_TextView_Time)
    TextView NewsDetailsTextViewTime;
    @BindView(R.id.News_Details_ProgressBar)
    ProgressBar NewsDetailsProgressBar;
    @BindView(R.id.News_Details_WebView)
    WebView NewsDetailsWebView;
    @BindView(R.id.News_Details_TextView_Date)
    TextView NewsDetailsTextViewDate;
    FrameLayout frameLayout;

    private String mUrl, mImage, mTitle, mDate, mSource, mAuthor;
    private boolean isHideToolbarView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);

        frameLayout = (FrameLayout) findViewById(R.id.date_behavior);
        setSupportActionBar(NewsDetailsToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        NewsDetailsAppBarLayout.addOnOffsetChangedListener(this);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mImage = intent.getStringExtra("image");
        mTitle = intent.getStringExtra("title");
        mDate = intent.getStringExtra("date");
        mSource = intent.getStringExtra("source");
        mAuthor = intent.getStringExtra("author");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(Utils.getRandomDrawbleColor());
        Glide.with(this).load(mImage).apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(NewsDetailsImageViewBackDrop);

        NewsDetailsTextViewTitleAppbar.setText(mSource);
        NewsDetailsTextViewSubtitleAppbar.setText(mUrl);
        NewsDetailsTextViewDate.setText(Utils.DateFormat(mDate));
        NewsDetailsTextViewTitle.setText(mTitle);

        String author = null;
        if (mAuthor != null || mAuthor != "") {
            author = " \u2022" + mAuthor;
        } else {
            author = "";
        }
        NewsDetailsTextViewTime.setText(mSource + author + " \u2022" + Utils.DateToTimeFormat(mDate));

        initWebView(mUrl);

    }

    public void initWebView(String url) {
        NewsDetailsWebView.getSettings().getLoadsImagesAutomatically();
        NewsDetailsWebView.getSettings().setJavaScriptEnabled(true);
        NewsDetailsWebView.getSettings().setDomStorageEnabled(true);
        NewsDetailsWebView.getSettings().setSupportZoom(true);
        NewsDetailsWebView.getSettings().setBuiltInZoomControls(true);
        NewsDetailsWebView.getSettings().setDisplayZoomControls(false);
        NewsDetailsWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        NewsDetailsWebView.setWebViewClient(new WebViewClient());
        NewsDetailsWebView.loadUrl(url);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        /*int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            frameLayout.setVisibility(View.GONE);
            NewsDetailsTextViewTitleAppbar.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        } else  {
            frameLayout.setVisibility(View.VISIBLE);
            NewsDetailsTextViewTitleAppbar.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
        }*/
        if (verticalOffset == 0) {
            frameLayout.setVisibility(View.VISIBLE);
            NewsDetailsTextViewSubtitleAppbar.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
        } else {
            frameLayout.setVisibility(View.GONE);
            NewsDetailsTextViewSubtitleAppbar.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.News_Details_Menu_share:
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plan");
                    intent.putExtra(Intent.EXTRA_SUBJECT, mSource);
                    String body = mTitle + "\n" + mUrl + "\n" + "Share From the News App" + "\n";
                    intent.putExtra(Intent.EXTRA_TEXT,body);
                    startActivity(Intent.createChooser(intent,"Share with :"));
                } catch (Exception e) {
                    Toast.makeText(this, "Hmm...Sorry,  \nCannot be Share", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.News_Details_Menu_Browser:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mUrl));
                startActivity(i);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
