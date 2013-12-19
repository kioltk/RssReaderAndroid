package com.agcy.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agcy.reader.core.Feedler;
import com.agcy.reader.core.Imager;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class StartActivity extends Activity {
    Context context;
    static Activity activity;
    View statusView;
    View loginView;
    TextView statusText;
    View statusBar;
    Button signinFeedly;
    Button statusButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        context = this;
        activity = this;
        statusView = (View) findViewById(R.id.statusView);
        statusBar =  findViewById(R.id.startStatusBar);
        statusText = (TextView) findViewById(R.id.startStatusText);
        loginView = (View) findViewById(R.id.loginView);
        signinFeedly = (Button) findViewById(R.id.signinFeedly);
        statusButton = (Button) findViewById(R.id.startStatusButton);

        //long id = storage.create();
        //long id2 = storage.get(1);

        Feedler.initialization(context);

        Imager.setImageLoading((ImageView) statusBar);
        if(Feedler.isLogined()){

            Toast.makeText(context, "мы залогинены, обновляем ключ", 2).show();
            Feedler.updateLogin(loginedStartHandler());
            statusViewShow("Connecting To Feedly");

        }
        else{

            loginViewShow();


            signinFeedly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SigninActvitiy.class);
                    startActivity(intent);
                }
            });
        }


    }
    public void statusViewShow(String message){
        loginView.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
        statusText.setText(message);

    }
    public void loginViewShow(){
        statusView.setVisibility(View.GONE);
        loginView.setVisibility(View.VISIBLE);
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


                Toast.makeText(context, response, 2).show();
                Feedler.initialization(lr);
                Intent main = new Intent(context, MainActivity.class);
                startActivity(main);
                activity.finish();
            }

            @Override
            public void onFailure(Throwable e, String response) {
                Toast.makeText(context, response, 2).show();
                statusBar.setVisibility(View.GONE);
                statusText.setText("Check your internet connection");
                statusButton.setVisibility(View.VISIBLE);
                statusButton.setText("Retry");
                statusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        statusButton.setVisibility(View.GONE);
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
