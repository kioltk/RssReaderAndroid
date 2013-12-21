package com.agcy.reader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.agcy.reader.CustomViews.SuperImageView;
import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.Models.Feedly.Stream;
import com.agcy.reader.core.Feedly.Categories;
import com.agcy.reader.core.Feedly.Comparators.EntryComparator;
import com.agcy.reader.core.Feedly.Entries;
import com.agcy.reader.core.Feedly.Feeds;
import com.agcy.reader.core.Parser;
import com.agcy.reader.core.Speaker;

import java.util.ArrayList;
import java.util.Collections;

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
    Stream source;
    static Context context;
    Entry currentEntry;
    ImageButton readButton;
    static View selectedEntryFullView;
    static View selectedEntryTileView;
    // TODO: прибить читалку к каждому итему
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        context = this;
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
        String sourceId = bundle.getString("sourceId");
        String sourceType = bundle.getString("sourceType");
        if (sourceType.equals("category"))
            source = Categories.getStream(sourceId);
        else
            source = Feeds.getStream(sourceId);
        if(Speaker.isSpeaking()){
            readButton.setImageResource(R.drawable.play_button);
        }else{
            readButton.setImageResource(R.drawable.pause_button);
        }
        /*
        final Feedler.EntryLoader task = new Feedler.EntryLoader(source.id, sourceType) {
            @Override
            public void onPostExecute(String result) {

                data = result;
                chewData();
                if (sourceType.equals("category"))
                    source = Categories.getStream(sourceId);
                else
                    source = Feeds.getStream(sourceId);
                entriesAdapter.updateItems(source.items);

            }
        };*/
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Speaker.isSpeaking()){
                    readButton.setImageResource(R.drawable.pause_button);
                    Speaker.stop();

                }else{
                    if(currentEntry != null){
                        readButton.setImageResource(R.drawable.play_button);
                        Speaker.speak(currentEntry);
                        Speaker.setProgressListener(entrySpeaker());
                    }
                }
            }
        });
        if(source.items.isEmpty()){
            //task.start();
        }
        else{
            Collections.sort(source.items, new EntryComparator.byDateLast());
            entriesAdapter.updateItems(source.items);
        }

        //
        // Set up the ViewPager with the sections adapter.

    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public UtteranceProgressListener entrySpeaker(){
        return new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                readButton.setImageResource(R.drawable.play_button);
            }

            @Override
            public void onDone(String utteranceId) {

                readButton.setImageResource(R.drawable.pause_button);
                ArrayList arrayList = new ArrayList<String>();
                arrayList.add(Speaker.currentEntry.id);
                /** todo:отмечать как прочитано когда читалка дочитала
                Feedler.ReadMarker marker = new Feedler.ReadMarker("markAsRead","entries",arrayList){
                    @Override
                    public void onPostExecute(String response){
                        Toast.makeText(context, "Marked as read", 1500).show();
                    }
                };
                marker.start();
                */

                Entries.markAsRead(currentEntry.id);
            }

            @Override
            public void onError(String utteranceId) {

                Toast.makeText(context, "error", 1500).show();
            }
        };
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
            currentEntry = source.items.get(0);
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
            View rootView = inflater.inflate(R.layout.tile_entry, container, false);
            selectedEntryTileView = (View) rootView.findViewById(R.id.entryTileView);
            selectedEntryFullView = (View) rootView.findViewById(R.id.entryFullView);

            TextView titleView = (TextView) rootView.findViewById(R.id.entryFullTitle);
            ViewGroup contentView = (ViewGroup) rootView.findViewById(R.id.entryFullContent);
            TextView dateView = (TextView) rootView.findViewById(R.id.entryFullDate);
            TextView tileTitle = (TextView) rootView.findViewById(R.id.tileTitle);
            TextView tileDate = (TextView) rootView.findViewById(R.id.tileDate);
            TextView originView = (TextView) rootView.findViewById(R.id.origin);
            SuperImageView tileImageView = (SuperImageView) rootView.findViewById(R.id.tileImage);
            SuperImageView imageView = (SuperImageView) rootView.findViewById(R.id.image);
            tileTitle.setText(entry.title);
            tileDate.setText(entry.dateLocale());
            titleView.setText(entry.title);

            originView.setText(entry.feed().title);
            dateView.setText(String.valueOf(entry.dateLocale()));


            String content = entry.summary.content;

            final float scale = context.getResources().getDisplayMetrics().widthPixels;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)scale-80, ViewGroup.LayoutParams.WRAP_CONTENT);
            contentView.addView(Parser.parseHtml(content),0);
            contentView.setLayoutParams(params);

            if (entry.visual.url!=null){
                imageView.setImageUrl(entry.visual.url);
                tileImageView.setImageUrl(entry.visual.url);
                tileImageView.setOnClickListener(new View.OnClickListener() {
                    View currentEntryTileView = selectedEntryTileView;
                    View currentEntryFullView = selectedEntryFullView;
                    @Override
                    public void onClick(View v) {
                        this.currentEntryTileView.setVisibility(View.GONE);
                        this.currentEntryFullView.setVisibility(View.VISIBLE);
                    }
                });
            }else{
                imageView.setVisibility(View.GONE);
                selectedEntryTileView.setVisibility(View.GONE);
                selectedEntryFullView.setVisibility(View.VISIBLE);
            }

            return rootView;
        }
    }

}
