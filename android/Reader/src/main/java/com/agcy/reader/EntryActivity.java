package com.agcy.reader;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.core.Feedler;
import com.agcy.reader.core.Feedly.Feeds;
import com.agcy.reader.core.Speaker;
import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;

public class EntryActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    EntryPagerAdapter entriesAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager entryViewPager;
    Feed feed;
    Context context;
    Entry currentEntry;
    ImageButton readButton;
    TextView feedNameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        context = this;
        feedNameView = (TextView) findViewById(R.id.feedNameView);
        readButton = (ImageButton) findViewById(R.id.readButton);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        entriesAdapter = new EntryPagerAdapter(getFragmentManager());

        entryViewPager = (ViewPager) findViewById(R.id.pager);
        entryViewPager.setAdapter(entriesAdapter);

        entryViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                currentEntry = entriesAdapter.getEntry(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });



        Bundle bundle = this.getIntent().getExtras();
        String feedId = bundle.getString("feedId");
        feed = Feeds.get(feedId);
        feedNameView.setText(feed.title);
        if(Speaker.isSpeaking()){
            readButton.setImageResource(R.drawable.play_button);

        }else{
            readButton.setImageResource(R.drawable.pause_button);
        }
        final Feedler.entryLoader task = new Feedler.entryLoader(feed.id) {
            @Override
            public void onPostExecute(String result) {

               // loadingStatus.setText("Loaded");
                data = result;
                chewData();
                entriesAdapter.updateItems(feed.entries);

            }
        };
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Speaker.isSpeaking()){
                    readButton.setImageResource(R.drawable.pause_button);
                    Speaker.stop();

                }else{
                    if(currentEntry != null){
                        readButton.setImageResource(R.drawable.play_button);
                        Speaker.speak(currentEntry.content());
                    }
                }
            }
        });
        if(feed.entries.isEmpty()){
            //loadingStatus.setText("Loading");
            task.start();
        }
        else{
            //loadingStatus.setText("Feed are ready");
            entriesAdapter.updateItems(feed.entries);
        }

        //
        // Set up the ViewPager with the sections adapter.

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.entry, menu);
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class EntryPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<Entry> items;
        public EntryPagerAdapter(FragmentManager fm) {
            super(fm);
            items = new ArrayList<Entry>();

        }

        public void updateItems(ArrayList<Entry> newEntries){
            items = newEntries;
            currentEntry = feed.entries.get(0);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return new EntryFragment(items.get(position));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return items.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        public Entry getEntry(int i) {
            return items.get(i);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class EntryFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String ARG_SECTION_NUMBER = "section_number";
        private final Entry entry;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        public EntryFragment(Entry entry) {
            this.entry = entry;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_entry, container, false);
            TextView titleView = (TextView) rootView.findViewById(R.id.entryTitle);
            titleView.setText(entry.title);
            TextView contentView = (TextView) rootView.findViewById(R.id.entryContent);
            contentView.setText(entry.content());
            TextView dateView = (TextView) rootView.findViewById(R.id.entryDate);
            dateView.setText(String.valueOf(entry.published));
            if (entry.visual!=null){
                SmartImageView imageView = (SmartImageView) rootView.findViewById(R.id.entryImage);
                imageView.setImageUrl(entry.visual.url);
            }
            return rootView;
        }
    }

}
