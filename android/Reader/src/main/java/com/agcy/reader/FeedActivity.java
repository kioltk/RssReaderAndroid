package com.agcy.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agcy.reader.CustomViews.SimpleFeedAdapter;
import com.agcy.reader.core.Feedler;
import com.agcy.reader.core.Feedly.Feeds;

public class FeedActivity extends Activity  {
    private int MY_DATA_CHECK_CODE = 0;

    Context context;
    ListView feedView;
    SimpleFeedAdapter feedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


        context = this;

        feedListAdapter = new SimpleFeedAdapter(this);

        feedView = (ListView) findViewById(R.id.feedView);
        feedView.setAdapter(feedListAdapter);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        final Feedler.FeedLoader task = new Feedler.FeedLoader() {
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

                Bundle basket = new Bundle();
                basket.putString("feedId", feedListAdapter.getItem(position).id);

                Intent intent = new Intent(context, EntryActivity.class);
                intent.putExtras(basket);
                startActivity(intent);
            }
        });

        /*loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            };


        });
        task.start();*/
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
        /*
        if (id == R.id.action_logout) {
            Feedler.logout();
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }




}
