package com.agcy.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agcy.reader.core.Feedler;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class StartActivity extends Activity {
    Context context;
    Activity activity;
    TextView statusText;
    ProgressBar statusBar;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        context = this;
        activity = this;

        statusBar = (ProgressBar) findViewById(R.id.startStatusBar);
        statusText = (TextView) findViewById(R.id.startStatusText);
        loginButton = (Button) findViewById(R.id.signinbutton);

        SharedPreferences access = getSharedPreferences("access", Context.MODE_PRIVATE);

        Feedler.setAccess(access);
        if(Feedler.isLogined()){

            Feedler.updateLogin(loginedStartHandler());
            statusText.setText("Connecting To Feedly");

        }
        else{

            statusText.setText("Well. Now we have to login");
            statusBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);



            loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SigninActvitiy.class);
                            startActivity(intent);
                        }
            });
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }
    private AsyncHttpResponseHandler loginedStartHandler(){
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
                statusBar.setVisibility(View.GONE);
                statusText.setText("Check your internet connection");
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText("Retry");
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        loginButton.setVisibility(View.GONE);
                        statusBar.setVisibility(View.VISIBLE);
                        statusText.setText("Connecting To Feedly");

                        Feedler.updateLogin(loginedStartHandler());
                    }
                });
            }

            @Override
            public void onFinish() {

            }
        };
    }

}
