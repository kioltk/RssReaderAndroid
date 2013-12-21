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


        Feedler.initialization(context);
        Imager.setImageLoading((ImageView) statusBar);

        if(Feedler.isLogined()){


            Intent main = new Intent(context, MainActivity.class);
            startActivity(main);

            Toast.makeText(context, "мы залогинены, обновляем ключ", Toast.LENGTH_SHORT).show();

            statusViewShow("Connecting To Feedly");
            activity.finish();
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


}
