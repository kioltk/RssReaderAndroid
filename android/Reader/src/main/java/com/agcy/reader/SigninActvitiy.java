package com.agcy.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agcy.reader.core.Feedler;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SigninActvitiy extends Activity {

    Context context;
    Activity activity;
    WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);



        String url = Feedler.getLoginUrl();

        myWebView = (WebView) findViewById(R.id.webview);
        MyWebViewClient client = new MyWebViewClient();
        myWebView.setWebViewClient(client);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);

        context = this;
        activity = this;

    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("localhost")){
                if (url.contains("error")){
                    Intent refresh = new Intent(context, StartActivity.class);
                    startActivity(refresh);
                    activity.finish();
                }
                else{
                    TextView loginStatusText = (TextView) findViewById(R.id.loginStatusText);
                    ProgressBar loginStatusBar = (ProgressBar) findViewById(R.id.loginStatusBar);

                    myWebView.setVisibility(View.GONE);
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
    }

    private AsyncHttpResponseHandler endLoginHandler(){
        return new AsyncHttpResponseHandler(){
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(String response) {
            Feedler.LoginResponse lr = new Gson().fromJson(response,Feedler.LoginResponse.class);
            Feedler.setUpdateToken(lr.refresh_token);
            Feedler.setToken(lr.access_token);
            Feedler.updateIn(lr.expires_in);
            //String st = Feedler.getFeed();
        }

        @Override
        public void onFailure(Throwable e, String response) {
        }

        @Override
        public void onFinish() {
            Intent main = new Intent(context, MainActivity.class);
            startActivity(main);
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
