package com.example.daniel.liquid_democracy;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class GoogleSearchFragment extends AppCompatActivity {

    private WebView webView;
    private ImageView ImageView;
    private ProgressBar ProgressBar;

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_googlesearch);

        //finders
        webView = findViewById(R.id.webView);
        ImageView = findViewById(R.id.myImageView);
        ProgressBar = findViewById(R.id.myProgressBar);
        back = findViewById(R.id.back_button);

        //set Progress bar
        ProgressBar.setMax(100);

        //set web info
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(MainActivity.weburl_generalsearch);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view,newProgress);
                ProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                ImageView.setImageBitmap(icon);
                ProgressBar.setProgress(0);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }
        else{
            finish();
        }
    }
}
