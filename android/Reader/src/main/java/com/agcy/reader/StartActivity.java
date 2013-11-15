package com.agcy.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.agcy.reader.core.Feedler;

public class StartActivity extends Activity {
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //SharedPreferences sp = getSharedPreferences("access",0);
        //String tok = sp.getString("access_token","");
        //if(tok.equals(""))
        {
            c = this;
            Feedler f = new Feedler();
            Button button = (Button) findViewById(R.id.signinbutton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, SigninActvitiy.class);
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
    
}