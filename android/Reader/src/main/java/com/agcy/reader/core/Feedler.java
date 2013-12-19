package com.agcy.reader.core;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.agcy.reader.Models.Feedly.Category;
import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.Models.Feedly.Stream;
import com.agcy.reader.core.Feedly.Categories;
import com.agcy.reader.core.Feedly.Entries;
import com.agcy.reader.core.Feedly.Feeds;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class Feedler{
    public static SharedPreferences accessStorage;
    public static SharedPreferences dataStorage;
    public static Context context;
    public static String url;
    private static String redirect_uri = "http://localhost";
    private static String client_id = "sandbox";
    private static String client_secret = "QNFQRFCFM1IQCJB367ON";
    private static String refresh_token;
    private static String access_token;

    public static void initialization(){
        Categories.initalization();
        Feeds.initalization();
        Entries.initalization();
    }
    public static void initialization(LoginResponse loginResponse) {
        if(loginResponse.refresh_token!=null)
            Feedler.setUpdateToken(loginResponse.refresh_token);
        Feedler.setToken(loginResponse.access_token);
        Feedler.updateIn(loginResponse.expires_in);
        initialization();
    }
    public static void initialization(Context context){
        Feedler.context = context;
        Imager.initialization(context);
        Parser.initialization(context);
        Notificator.initialization(context);
        Feedler.accessStorage = context.getSharedPreferences("Access",Context.MODE_PRIVATE);
        refresh_token  = Feedler.accessStorage.getString("refresh_token", "");

    }

    static ContentValues tempValues = new ContentValues();
    public static void saveAccess(){
        SharedPreferences.Editor editor = Feedler.accessStorage.edit();
        editor.putString("refresh_token", refresh_token);
        editor.apply();
    }
    public static void saveData(){

        Storage.Saver saver = new Storage.Saver() {
            @Override
            public void toSave() {
                long time = new Date().getTime();
                Log.i("agcylog", "сохранение инфы на диск " + time);

                for (Category category : Categories.getCategories()) {
                    saveCategory(category);
                }

                for (Feed feed : Feeds.list()) {
                    saveFeed(feed);
                }

                for (Entry entry : Entries.list()) {
                    saveEntry(entry);
                }

                //Storage.end();
                Log.i("agcylog", "сохранено за " + (new Date().getTime() - time));
            }
        };
        Storage.savers.add(saver);
        saver.start();
    }
    private static void saveEntry(Entry entry) {
        tempValues.clear();
        tempValues.put("id", entry.id);
        tempValues.put("content", entry.summary.content);
        tempValues.put("title", entry.title);
        tempValues.put("unread", entry.unread);
        tempValues.put("streamId", entry.origin.streamId);
        tempValues.put("image", entry.visual.url);
        tempValues.put("date", entry.published);
        Storage.save("entry", tempValues);
    }
    private static void saveCategory(Category category) {
        tempValues.clear();
        tempValues.put("id", category.id);
        tempValues.put("label", category.label);
        Storage.save("category", tempValues);
    }
    public static void saveFeed(Feed feed){
        tempValues.clear();
        tempValues.put("id", feed.id);
        tempValues.put("title", feed.title);
        tempValues.put("website", feed.website);
        tempValues.put("categories", feed.getCategories());
        Storage.save("feed", tempValues);
        tempValues.clear();
        tempValues.put("feedid", feed.id);

        for (Category category : feed.categories) {
            tempValues.put("categoryid", category.id);
            Storage.save("feedCategories", tempValues);
        }
    }
    public static void saveEntryAsync(final Entry entry){
        Storage.Saver saver = new Storage.Saver() {
            @Override
            public void toSave() {
                saveEntry(entry);
            }
        };
        Storage.savers.add(saver);
        saver.start();
    }
    public static void saveFeedAsync(final Feed feed){
        Storage.Saver saver = new Storage.Saver() {
            @Override
            public void toSave() {
                saveFeed(feed);
            }
        };

        Storage.savers.add(saver);
        saver.start();
    }
    public static void saveCategoryAsync(final Category category){
        Storage.Saver saver = new Storage.Saver() {
            @Override
            public void toSave() {
                saveCategory(category);
            }
        };
        Storage.savers.add(saver);
        saver.start();
    }
    public static void saveCategoriesAsync(final ArrayList<Category> categories){
        Storage.Saver saver = new Storage.Saver() {
            @Override
            public void toSave() {
                for(Category category:categories)
                    saveCategory(category);
            }
        };
        Storage.savers.add(saver);
        saver.start();
    }
    public static void saveFeedsAsync(final ArrayList<Feed> feeds){
        Storage.Saver saver = new Storage.Saver() {
            @Override
            public void toSave() {
                for(Feed feed:feeds)
                    saveFeed(feed);
            }
        };
        Storage.savers.add(saver);
        saver.start();
    }
    public static void saveEntriesAsync(final ArrayList<Entry> entries){
        Storage.Saver saver = new Storage.Saver() {
            @Override
            public void toSave() {
                for(Entry entry:entries)
                    saveEntry(entry);
            }
        };
        Storage.savers.add(saver);
        saver.start();
    }
    public static Boolean loadData(){

        long time = new Date().getTime();
        Log.i("agcylog", "загрузка с диска" + time);
        Cursor cursor;
        Boolean loaded = false;

        cursor = Storage.get("category");
        Log.i("agcylog", "курсор взят через " + (new Date().getTime()-time));
        if(cursor.moveToFirst()){
            do {
                Category category = new Category();
                category.id = cursor.getString(cursor.getColumnIndex("id"));
                category.label = cursor.getString(cursor.getColumnIndex("label"));
                Categories.add(category);
            }
            while (cursor.moveToNext());
            loaded= true;

            Log.i("agcylog", "категории загружены через " + (new Date().getTime()-time));
        }

        cursor = Storage.get("feed");
        if(cursor.moveToFirst()){
            do {
                Feed feed = new Feed();
                feed.id = cursor.getString(cursor.getColumnIndex("id"));
                feed.website = cursor.getString(cursor.getColumnIndex("website"));
                feed.title = cursor.getString(cursor.getColumnIndex("title"));
                //feed.setCategories(cursor.getString(cursor.getColumnIndex("categories")));
                Feeds.add(feed);
            }while (cursor.moveToNext());
            loaded = true;
            Log.i("agcylog", "фиды загружены через " + (new Date().getTime()-time));
        }

        cursor = Storage.get("entry");
        int a = cursor.getColumnIndex("title"),
                b = cursor.getColumnIndex("id"),
                c= cursor.getColumnIndex("content"),
                d = cursor.getColumnIndex("streamId"),
                e = cursor.getColumnIndex("unread"),
                f = cursor.getColumnIndex("image"),
                g = cursor.getColumnIndex("date");

        String s = "";
        if(cursor.moveToFirst()){
            do {

                Entry entry = new Entry();
                entry.title = cursor.getString(a);
                Log.i("agcylog", "статья " + s + " загружена через " + (new Date().getTime()-time));
                entry.id = cursor.getString(b);
                entry.summary.content = cursor.getString(c);
                entry.origin.streamId = cursor.getString(d);
                entry.unread = cursor.getInt(e)>0;
                entry.visual.url = cursor.getString(f);


                entry.published  = cursor.getLong(g);
                Entries.add(entry);

            }while (cursor.moveToNext());
            loaded = true;
            Log.i("agcylog", "статьи загружены через " + (new Date().getTime()-time));
        }

        cursor = Storage.get("feedCategories");
        if(cursor.moveToFirst()){
            do{

                Feed feed =  Feeds.get(cursor.getString(0));
                Category category = Categories.get(cursor.getString(1));
                feed.categories.add(category);
                category.addFeed(feed);

            } while (cursor.moveToNext());
        }

        Log.i("agcylog", "Загружено за " + (new Date().getTime()-time));
        return loaded;
    }

    public static void logout() {

        for(Loader loader:downloaders){
            loader.cancel(true);
        }
        downloaders.clear();
        categoryDownloader.cancel(true);
        feedDownloader.cancel(true);
        for(AsyncTask task:Imager.imageloadingTasks.values()){
            task.cancel(true);
        }
        Imager.imageloadingTasks.clear();

        SharedPreferences.Editor editor = accessStorage.edit();
        editor.remove("refresh_token");
        editor.apply();
        accessStorage = null;
        Feeds.clear();
        Categories.clear();
        Entries.clear();
        refresh_token = null;
        access_token = null;
        context.deleteDatabase("feedly.db");
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCookiesChromium.db");
        context = null;
    }

    public static Boolean isLogined() {
        if (refresh_token.equals("")){
            return false;
        }
        return true;
    }

    public static String getLoginUrl(){

        String loginUrl ="http://sandbox.feedly.com/v3/auth/auth?response_type=code";
        loginUrl = loginUrl +
                "&client_id="+client_id +
                "&redirect_uri="+redirect_uri+
                "&scope=https://cloud.feedly.com/subscriptions";

        return loginUrl;
    }
    public static void endLogin(String requestCode,AsyncHttpResponseHandler handler) {
        String grant_type;
        String loginUrl ="http://sandbox.feedly.com/v3/auth/token?";

        grant_type = "authorization_code";
        loginUrl = loginUrl +
                "code="+ requestCode +
                "&client_id="+client_id+
                "&client_secret="+client_secret+
                "&redirect_uri="+redirect_uri+
                "&grant_type="+grant_type;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        client.post(loginUrl, handler);
    }
    public static void updateLogin(AsyncHttpResponseHandler handler) {

        String grant_type;
        String updateUrl ="http://sandbox.feedly.com/v3/auth/token?";
        grant_type = "refresh_token";
        updateUrl = updateUrl +
                "refresh_token="+ refresh_token +
                "&client_id="+client_id+
                "&client_secret="+client_secret+
                "&redirect_uri="+redirect_uri+
                "&grant_type="+grant_type;
        AsyncHttpClient client = new AsyncHttpClient();


        client.post(updateUrl, handler);
    }
    public static void updateIn(int expires_in) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                AsyncHttpResponseHandler backgroundUpdate = new AsyncHttpResponseHandler(){
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(String response) {
                        Feedler.LoginResponse lr = new Gson().fromJson(response,Feedler.LoginResponse.class);
                        Feedler.setToken(lr.access_token);
                        Feedler.updateIn(lr.expires_in);
                    }

                    @Override
                    public void onFailure(Throwable e, String response) {
                    }

                    @Override
                    public void onFinish() {

                    }
                };
                updateLogin(backgroundUpdate);
            }
        }, expires_in*1000);

    }

    public static String getToken(){
        return access_token;
    }
    public static void setToken(String _access_token) {
        access_token = _access_token;
    }
    public static void setUpdateToken(String _refresh_token) {
        refresh_token = _refresh_token;
        saveAccess();
    }
    private static ArrayList<EntryLoader> downloaders = new ArrayList<EntryLoader>();

    public static void downloadEntries() {

        final long time = new Date().getTime();
        Log.i("agcylog", "скачка статей " + time);
        for(Feed feed:Feeds.list()){
            EntryLoader entryLoader = new EntryLoader(feed.id,"feed"){
                @Override
                public void onPostExecute(String result) {
                    Log.i("agcylog", "фид загружен" + result);

                    data = result;
                    chewData();
                    entryDownloaded();
                    downloaders.remove(this);
                    if(downloaders.isEmpty()){
                        saveData();
                        Log.i("agcylog", "скачано "+Entries.list().size()+" статей за " + time);

                    }
                    else{

                        Log.i("agcylog", "начало загрузки фида " + downloaders.get(0).sourceId);
                        downloaders.get(0).start();

                    }
                }
            };
            downloaders.add(entryLoader);
        }
        if(!downloaders.isEmpty()){
            downloaders.get(0).start();

            Log.i("agcylog", "начало загрузки статьи");
        }
    }
    private static void entryDownloaded(){
        //Feedler.saveData();
    }
    public static CategoryLoader categoryDownloader;
    public static FeedLoader feedDownloader;

    public class LoginResponse{
        public String id;
        public String  access_token;
        public String refresh_token;
        public int expires_in;
        public String token_type;
        public String plan;
        public String state;
        public LoginResponse(){

        }
    }

    public static class FeedLoader extends Loader{
        public FeedLoader() {
            super();
            methodName = "subscriptions";
        }
        public String data ="";
        public void chewData(){
            ArrayList<Feed> s = new Gson().fromJson(data, new TypeToken<ArrayList<Feed>>() {
            }.getType());
            Feeds.add(s);

            saveFeedsAsync(s);
        }
    }
    public static class EntryLoader extends Loader{
        public String sourceId;
        public String sourceType;
        public EntryLoader(String sourceId,String sourceType  ) {
            super();
            this.sourceId = sourceId;
            this.sourceType = sourceType;
            methodName = "streams/contents";
            parameters.put("streamId",sourceId);
        }
        public String data ="";
        public void chewData(){

            Stream s = new Gson().fromJson(data, Stream.class);
            Entries.add(s.items);
            //saveEntriesAsync(s.items);
        }
    }
    public static class CategoryLoader extends Loader{
        public CategoryLoader() {
            super();
            baseUrl = "http://sandbox.feedly.com/v3/";
            methodName ="categories";
        }
        public String data ="";
        public void chewData(){
            TypeToken<ArrayList<Category>> tok = new TypeToken<ArrayList<Category>>() {
            };
            Type type = tok.getType();
            Gson gson = new Gson();

            try {
                ArrayList<Category> s = gson.fromJson(data, type);
                Categories.add(s);
                saveCategoriesAsync(s);
            } catch (JsonSyntaxException e) {
                String s = e.getMessage();
            }

        }
    }
    public static class ReadMarker extends Loader{
        public static class Marker{
            public String action;
            public String type;
            public ArrayList<String> entryIds;
            public ArrayList<String> feedIds;
            public ArrayList<String> categoryIds;
            public Marker(){

            }
        }

        public ReadMarker(String action,String type,ArrayList<String> list) {
            super();
            Marker marker = new Marker();
            marker.action = action;
            marker.type = type;
            if(type.equals("entries"))
                marker.entryIds = list;
            else
                if(type.equals("feeds"))
                    marker.feedIds = list;
                else{
                    marker.categoryIds = list;
                }
            requestData = marker;
            this.methodType = "POST";
            baseUrl = "http://sandbox.feedly.com/v3/markers";
        }
        public String data ="";
        public void chewData(){
           /* TypeToken<ArrayList<Category>> tok = new TypeToken<ArrayList<Category>>() {
            };
            Type type = tok.getType();
            Gson gson = new Gson();

            try {
                ArrayList<Category> s = gson.fromJson(data, type);
                Categories.add(s);
            } catch (JsonSyntaxException e) {
                String s = e.getMessage();
            }
            */

        }
    }


}
