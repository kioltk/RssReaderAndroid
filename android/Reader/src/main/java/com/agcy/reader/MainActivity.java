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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agcy.reader.CustomViews.SimpleFeedListAdapter;
import com.agcy.reader.Models.RssChannel;
import com.agcy.reader.core.Feedler;
import com.agcy.reader.core.Feedler.feedLoader;
import com.agcy.reader.core.Feedly.Feeds;
import com.agcy.reader.core.Speaker;

import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private int MY_DATA_CHECK_CODE = 0;
    RssChannel channel;
    Context context;
    TextView connView;
    ListView feedView;
    SimpleFeedListAdapter feedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        connView = (TextView) findViewById(R.id.conn);

        feedListAdapter = new SimpleFeedListAdapter(this);

        feedView = (ListView) findViewById(R.id.feedView);
        feedView.setAdapter(feedListAdapter);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        connView.setText("Loading feeds");

        final feedLoader task = new Feedler.feedLoader(this) {
            @Override
            public void onPostExecute(String result) {
                data = result;
                chewData();
                feedListAdapter.updateItems(Feeds.list());
            }
        };





        feedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Speaker.speak("loading");
                Bundle basket = new Bundle();
                basket.putString("feedId", feedListAdapter.getItem(position).id);

                Intent intent = new Intent(context, EntryActivity.class);
                intent.putExtras(basket);
                startActivity(intent);
            }
        });

        Button loadButton = (Button) findViewById(R.id.loadButton);
        /*loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            };


        });*/
        loadButton.setVisibility(View.GONE);
        task.start();
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

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Feedler.logout();
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        /*readingPosition++;
                        RssItem item = channel.items.get(readingPosition);

                        Speaker.speak(item.description);*/

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
