package com.agcy.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.agcy.reader.core.Feedler;

public class SigninActvitiy extends Activity {

    Context c;Activity a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Feedler.startLogin();
        String url = Feedler.url;
        WebView myWebView = (WebView) findViewById(R.id.webview);
        MyWebViewClient client = new MyWebViewClient();
        myWebView.setWebViewClient(client);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
        c = this;
        a = this;
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("localhost")){
                if (url.contains("error")){
                    Intent refresh = new Intent(c, SigninActvitiy.class);
                    startActivity(refresh);
                    a.finish();
                }
                else{
                    String code = url.substring(url.indexOf("code=")+5,url.indexOf("&state"));
                    Feedler.endLogin(code);
                }
            }

            view.loadUrl(url);
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signin_actvitiy, menu);
        return true;
    }
    
}
