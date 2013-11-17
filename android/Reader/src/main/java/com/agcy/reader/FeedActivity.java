package com.agcy.reader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.agcy.reader.CustomViews.entrySimpleItemAdapter;
import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.core.Feedler;
import com.agcy.reader.core.Feedly.Feeds;
import com.agcy.reader.core.Speaker;

public class FeedActivity extends Activity {

    ListView entriesView;
    entrySimpleItemAdapter entriesAdapter;
    TextView loadingStatus;
    Button readButton;
    int readingPosition = 0;
    Feed feed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        entriesAdapter = new entrySimpleItemAdapter(this);
        entriesView = (ListView) findViewById(R.id.entriesView);
        entriesView.setAdapter(entriesAdapter);
        loadingStatus = (TextView) findViewById(R.id.feedLoadingStatusText);
        readButton = (Button) findViewById(R.id.readButton);


        Bundle bundle = this.getIntent().getExtras();
        String feedId = bundle.getString("feedId");
        feed = Feeds.get(feedId);



         final Feedler.entryLoader task = new Feedler.entryLoader(feed.id) {
            @Override
            public void onPostExecute(String result) {

                Speaker.speak("Loaded");
                loadingStatus.setText("Loaded");
                data = result;
                chewData();
                entriesAdapter.updateItems(feed.entries);
            }
        };
        if(feed.entries.isEmpty()){
            Speaker.speak("Loading");
            loadingStatus.setText("Loading");
            task.start();
        }
        else{
            Speaker.speak("Feed are ready");
            loadingStatus.setText("Feed are ready");
            entriesAdapter.updateItems(feed.entries);
        }

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!entriesAdapter.isEmpty()){
                    Entry item = entriesAdapter.getItem(readingPosition);
                    Speaker.speak(item.summary.content.substring(0,1000));
                }
            };


        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */


}
