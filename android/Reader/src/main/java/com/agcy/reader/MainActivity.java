package com.agcy.reader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agcy.reader.CustomViews.RssListAdapter;
import com.agcy.reader.Models.RssChannel;
import com.agcy.reader.Models.RssItem;
import com.agcy.reader.core.Feedler;
import com.agcy.reader.core.Parser;
import com.agcy.reader.core.Speaker;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private int MY_DATA_CHECK_CODE = 0;
    RssChannel channel;
    Context context;
    int readingPosition = 0;
    TextView connView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        connView = (TextView) findViewById(R.id.conn);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(){
            @Override
            public void onStart() {

                connView.setText("Started");
            }

            @Override
            public void onSuccess(String response) {


                connView.setText("success");
                Parser parser= new Parser(response);
                parser.execute();

                if(parser.status.equals("success"))
                {
                    channel = parser.response;
                    RssListAdapter adapter = new RssListAdapter(context);
                    adapter.updateItems(channel.items);
                    ListView rssFeedView = (ListView) findViewById(R.id.rssFeed);
                    rssFeedView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Throwable e, String response) {

                connView.setText("fail");
            }

            @Override
            public void onFinish() {

                // ((TextView)findViewById(R.id.status)).setText("Finished");
            }
        };

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);



        //Loader.getFeed(handler);
        String st= Feedler.getFeed();

        Button buttonRead = (Button) findViewById(R.id.readButton);
        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (channel!=null){




                    RssItem item = channel.items.get(readingPosition);

                    Speaker.speak(item.description);





                }
            };


        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
               Speaker.myTTS = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            {
                if(Speaker.myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)

                    Speaker.myTTS.setLanguage(Locale.US);
                UtteranceProgressListener voiceListener = new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        readingPosition++;
                        RssItem item = channel.items.get(readingPosition);

                        Speaker.speak(item.description);

                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                };
                Speaker.myTTS.setOnUtteranceProgressListener(voiceListener);
            }
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }


}
