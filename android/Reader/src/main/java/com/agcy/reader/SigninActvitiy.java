package com.agcy.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agcy.reader.core.Feedler;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SigninActvitiy extends Activity {

    Context context;
    Activity activity;
    TextView loginStatusText;
    ProgressBar loginStatusBar;
    WebView loginWebView;
    String loginStatus = "loading";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        context = this;
        activity = this;

        loginStatusBar = (ProgressBar) findViewById(R.id.loginStatusBar);
        loginStatusText = (TextView) findViewById(R.id.loginStatusText);
        loginWebView = (WebView) findViewById(R.id.loginWebView);

        String url = Feedler.getLoginUrl();

        MyWebViewClient client = new MyWebViewClient();
        loginWebView.setWebViewClient(client);
        WebSettings webSettings = loginWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        loginWebView.loadUrl(url);
        loginStatusText.setText("Loading");

    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("localhost")){
                if (url.contains("error")){
                    Intent refresh = new Intent(context, StartActivity.class);
                    startActivity(refresh);
                    activity.finish();
                    Toast.makeText(context, "Could not access", Toast.LENGTH_SHORT).show();
                }
                else{
                    TextView loginStatusText = (TextView) findViewById(R.id.loginStatusText);
                    ProgressBar loginStatusBar = (ProgressBar) findViewById(R.id.loginStatusBar);

                    loginStatus="finished";
                    loginWebView.setVisibility(View.GONE);
                    loginStatusText.setVisibility(View.VISIBLE);
                    loginStatusBar.setVisibility(View.VISIBLE);
                    loginStatusText.setText("Connecting to Feedly");
                    String code = url.substring(url.indexOf("code=")+5,url.indexOf("&state"));

                    Feedler.endLogin(code, endLoginHandler());
                }
            }

            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url){
            if(loginStatus.equals("loading")){
                loginStatusBar.setVisibility(View.GONE);
                loginStatusText.setVisibility(View.GONE);
                loginWebView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void	onPageStarted(WebView view, String url, Bitmap favicon){

                loginStatusBar.setVisibility(View.VISIBLE);
                loginStatusText.setVisibility(View.VISIBLE);
                loginWebView.setVisibility(View.GONE);

        }

    }

    private AsyncHttpResponseHandler endLoginHandler(){
        return new AsyncHttpResponseHandler(){
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(String response) {
            Feedler.LoginResponse lr = new Gson().fromJson(response,Feedler.LoginResponse.class);

            Feedler.initialization(lr);
            Intent main = new Intent(context, MainActivity.class);
            startActivity(main);
            activity.finish();
        }

        @Override
        public void onFailure(Throwable e, String response) {
        }

        @Override
        public void onFinish() {
        }
    };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signin_actvitiy, menu);
        return true;
    }
    
}
