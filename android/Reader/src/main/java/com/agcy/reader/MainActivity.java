package com.agcy.reader;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agcy.reader.CustomViews.LittleFeedsAdapter;
import com.agcy.reader.CustomViews.NavigationDrawerFragment;
import com.agcy.reader.CustomViews.SimpleCategoryAdapter;
import com.agcy.reader.CustomViews.SimpleEntryAdapter;
import com.agcy.reader.CustomViews.SimpleFeedAdapter;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.core.Feedler;
import com.agcy.reader.core.Feedly.Categories;
import com.agcy.reader.core.Feedly.Entries;
import com.agcy.reader.core.Feedly.Feeds;
import com.agcy.reader.core.Imager;
import com.agcy.reader.core.Speaker;
import com.agcy.reader.core.Storage;

import java.util.Locale;

;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, TextToSpeech.OnInitListener {

    static Context context;
    static Activity activity;
    static View contentView;
    static View statusView;
    static TextView statusText;
    static SimpleCategoryAdapter categoryListAdapter;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        activity = this;

        contentView =  findViewById(R.id.contentView);
        statusView =  findViewById(R.id.statusView);
        statusText = (TextView) findViewById(R.id.statusText);
        Log.i("agcylog","loaded");
        Storage.initialization(this);
        Imager.initialization(this);
        Feedler.categoryDownloader = new Feedler.CategoryLoader() {
            @Override
            public void onPostExecute(String result) {
                data = result;
                chewData();
                Feedler.feedDownloader.start();
            }
        };


        Feedler.feedDownloader = new Feedler.FeedLoader() {
            @Override
            public void onPostExecute(String result) {
                data = result;
                chewData();
                Feedler.downloadEntries();
                contentViewShow();
                categoryListAdapter.updateItems(Categories.list());
            }
        };


        if(!Feedler.loadData())
            Feedler.categoryDownloader.start();

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 0);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contentView, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = "Feeds";
                break;
            case 1:
                mTitle = "Categories";
                break;
            case 3:
                mTitle = "Entries";
                break;
            default:
                mTitle = "Settings";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

                Log.i("agcylog","ттс загружен нормально");
                Speaker.myTTS = new TextToSpeech(this, this);

            }
            else {
                Log.i("agcylog","ттс не загружен, выберите ттс");
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
                Log.i("agcylog","ттс выбран");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }


        SimpleFeedAdapter feedListAdapter;
        SimpleEntryAdapter simpleEntryAdapter;
        static String[] settingsArray =  new String[]{
                "Download images",
                "Logout"
        };
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView;
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 2:
                    rootView = FeedActivityView(inflater, container);
                    break;
                case 1:
                    rootView = CategoryActivityView(inflater,container);
                    break;
                case 3:
                    rootView = EntryActivityView(inflater,container);
                    break;
                default:
                    rootView = SettingsActivityView(inflater,container);
                    break;
            }

            return rootView;
        }
        ListView categoryView;
        public View SettingsActivityView(LayoutInflater inflater, ViewGroup container){

           View rootView= inflater.inflate(R.layout.fragment_settings, container, false);
           ListView settingsView;
           settingsView = (ListView) rootView.findViewById(R.id.settingsView);

           ArrayAdapter<String> settingsListAdapter = new ArrayAdapter<String>(
                   context,
                   android.R.layout.simple_list_item_activated_1,
                   android.R.id.text1,
                   settingsArray
                   );


           settingsView.setAdapter(settingsListAdapter);
           settingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   switch (position){
                       case 0:
                           Imager.downloadImages();
                           break;
                       case 1:
                           Feedler.logout();
                           Intent intent = new Intent(context, StartActivity.class);
                           startActivity(intent);
                           activity.finish();
                           break;
                   }

               }
           });

           return  rootView;
       }
        public View FeedActivityView(LayoutInflater inflater, ViewGroup container){

            View rootView= inflater.inflate(R.layout.fragment_feed, container, false);
            ListView feedView;
            statusViewShow("Loading feeds");
            feedListAdapter = new SimpleFeedAdapter(context);

            feedView = (ListView) rootView.findViewById(R.id.feedView);
            feedView.setAdapter(feedListAdapter);

            feedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Bundle basket = new Bundle();
                    basket.putString("sourceId", feedListAdapter.getItem(position).id);
                    basket.putString("sourceType", "feed");

                    Intent intent = new Intent(context, EntryActivity.class);
                    intent.putExtras(basket);
                    startActivity(intent);

                }
            });
            if (Feeds.list().isEmpty()){

            }
            else{
                feedListAdapter.updateItems(Feeds.list());
                contentViewShow();
            }
            return rootView;
        }
        public View EntryActivityView(LayoutInflater inflater, ViewGroup container){
            Log.i("agcylog","начинаем отображение статей");
            View rootView= inflater.inflate(R.layout.fragment_entry, container, false);
            ListView entryView;
            statusViewShow("Loading entries");
            simpleEntryAdapter = new SimpleEntryAdapter(context);

            entryView = (ListView) rootView.findViewById(R.id.entryView);
            entryView.setAdapter(simpleEntryAdapter);
            entryView.setItemsCanFocus(true);

            Log.i("agcylog","схватили все группы отображений");








            /*
            entryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle basket = new Bundle();
                    basket.putString("sourceId", simpleEntryAdapter.getItem(position).id);
                    basket.putString("sourceType", "entry");

                    Intent intent = new Intent(context, EntryActivity.class);
                    intent.putExtras(basket);
                    startActivity(intent);


                }
            });/* */
            if (Entries.list().isEmpty()){

                Log.i("agcylog","статей нет");
            }
            else{

                Log.i("agcylog","обновляем список");
                simpleEntryAdapter.updateItems(Entries.list());
                contentViewShow();
                Log.i("agcylog", "список обновлен");
            }

            return rootView;
        }
        public View CategoryActivityView(LayoutInflater inflater, ViewGroup container){
            View rootView= inflater.inflate(R.layout.fragment_category, container, false);

            statusViewShow("Category loading");
            categoryListAdapter = new SimpleCategoryAdapter(context);

            categoryView = (ListView) rootView.findViewById(R.id.categoryView);
            categoryView.setAdapter(categoryListAdapter);
            /*
            Loader loader = new Loader(){
                @Override
                public void onPostExecute(String result) {
                    String s = result;
                    Toast.makeText(context,s,1000).show();
                }
            };

            loader.start();

            categoryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(context,"position "+ position+ " id "+ id, 10).show();
                    return false;
                }
            });
            */


            categoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Bundle basket = new Bundle();
                    basket.putString("sourceId", categoryListAdapter.getId(position));
                    basket.putString("sourceType", "category");
                    Intent intent = new Intent(context, EntryActivity.class);
                    intent.putExtras(basket);
                    startActivity(intent);

                }

            });


           /* SwipeToMore.OnActionCallback callBack = new SwipeToMore.OnActionCallback() {
                @Override
                public SwipeToMore.Action onAction(AbsListView listView, int position) {

                    return null;

                }
            };
            SwipeToMore swipeList = new SwipeToMore(categoryView, callBack, SwipeToMore.UndoMode.COLLAPSED_UNDO);
            */
            /**/
            categoryListAdapter.setOnFeedClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v, int childPosition, long id) {
                    LittleFeedsAdapter feedsAdapter = (LittleFeedsAdapter) parent.getAdapter();

                    Bundle basket = new Bundle();
                    basket.putString("sourceId", ((Feed) feedsAdapter.getItem(childPosition)).id);
                    basket.putString("sourceType", "feed");

                    Intent intent = new Intent(context, EntryActivity.class);
                    intent.putExtras(basket);
                    startActivity(intent);
                }
            });








            if (Categories.list().isEmpty()){

            }
            else{
                categoryListAdapter.updateItems(Categories.list());
                contentViewShow();
            }






            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    public static void statusViewShow(String message){
        contentView.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
        statusText.setText(message);

    }
    public static void contentViewShow(){
        statusView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

}
