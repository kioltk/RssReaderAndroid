package com.agcy.reader.core;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.agcy.reader.Models.Feedly.Category;
import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.Models.Feedly.Stream;
import com.agcy.reader.core.Feedly.Categories;
import com.agcy.reader.core.Feedly.Entries;
import com.agcy.reader.core.Feedly.Feeds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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

    public static void initialization(LoginResponse loginResponse) {
        if(loginResponse.refresh_token!=null)
            Feedler.setUpdateToken(loginResponse.refresh_token);
        Feedler.setToken(loginResponse.access_token);
        Feedler.updateIn(loginResponse.expires_in);
    }
    public static void initialization(Context context)  {
        Feedler.context = context;
        Imager.initialization(context);
        Parser.initialization(context);
        Notificator.initialization(context);
        Feedler.accessStorage = context.getSharedPreferences("Access",Context.MODE_PRIVATE);
        refresh_token  = Feedler.accessStorage.getString("refresh_token", "");
        access_token = Feedler.accessStorage.getString("access_token","");

    }

    static ContentValues tempValues = new ContentValues();
    public static void saveAccess(){
        SharedPreferences.Editor editor = Feedler.accessStorage.edit();
        editor.putString("refresh_token", refresh_token);
        editor.putString("access_token",access_token);
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
        tempValues.put("lastupdate",feed.lastUpdate);
        Storage.save("feed", tempValues);
        tempValues.clear();
        tempValues.put("feedid", feed.id);

        for (Category category : feed.categories) {
            tempValues.put("categoryid", category.id);
            tempValues.put("id",Imager.codeFilename(feed.id+category.id));
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

            Log.i("agcylog", "категории загружены через " + (new Date().getTime()-time));
        }

        cursor = Storage.get("feed");
        if(cursor.moveToFirst()){
            do {
                Feed feed = new Feed();
                feed.id = cursor.getString(cursor.getColumnIndex("id"));
                feed.website = cursor.getString(cursor.getColumnIndex("website"));
                feed.title = cursor.getString(cursor.getColumnIndex("title"));
                feed.lastUpdate = cursor.getLong(cursor.getColumnIndex("lastupdate"));
                Feeds.add(feed);
            }while (cursor.moveToNext());
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
    public static void updateLogin() {
        AsyncHttpResponseHandler handler;
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
        handler = new AsyncHttpResponseHandler(){
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                Feedler.LoginResponse lr = new Gson().fromJson(response,Feedler.LoginResponse.class);
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                Feedler.initialization(lr);
            }

            @Override
            public void onFailure(Throwable e, String response) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {

            }
        };
        client.post(updateUrl, handler);
    }
    public static void updateIn(int expires_in) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                updateLogin();
            }
        }, expires_in*1000);

    }


    public static String getToken(){
        return access_token;
    }
    public static void setToken(String access_token) {
        Feedler.access_token = access_token;
        saveAccess();
    }
    public static void setUpdateToken(String refresh_token) {
        Feedler.refresh_token = refresh_token;
        saveAccess();
    }



    private static ArrayList<StreamLoader> downloaders = new ArrayList<StreamLoader>();
    public static CategoryLoader categoryDownloader;
    public static FeedLoader feedDownloader;
    // todo:выдача загрузчиков
    public static void downloadAll(){
        if(categoryDownloader==null && feedDownloader == null && downloaders.isEmpty()){
            Notificator.show("Downloadings processing","It should be faster..",0,0,true);
            categoryDownloader = new CategoryLoader();
            feedDownloader = new FeedLoader();

            categoryDownloader.addListener(new Loader.onEndListener() {
                @Override
                public void toDo(Object response) {
                    categoryDownloader = null;
                    feedDownloader.start();

                    Notificator.show("Downloading feeds","Wait a moment",0,0,true);
                }

                @Override
                public void onError(Loader.Error error) {

                }
            });
            feedDownloader.addListener(new Loader.onEndListener() {
                @Override
                public void toDo(Object response) {
                    feedDownloader = null;
                    downloadEntries();
                }

                @Override
                public void onError(Loader.Error error) {

                }
            });

            categoryDownloader.start();

            Notificator.show("Downloading categories","Wait a moment",0,0,true);
        }
    }
    public static void downloadEntries() {

        final long time = new Date().getTime();
        Log.i("agcylog", "скачка статей " + time);

        StreamLoader.Filter filter = new StreamLoader.Filter(){{ count=100; unreadOnly=true;}};
        final int size = Feeds.list().size();
        for(Feed feed:Feeds.list()){
            filter.newerThan = feed.lastUpdate;
            final StreamLoader entryLoader = new StreamLoader(feed.id,Stream.TYPE_FEED,filter);
            final Loader.onEndListener entriesLoadedListener = new Loader.onEndListener() {
                @Override
                public void toDo(Object response) {

                    downloaders.remove(entryLoader);

                    Notificator.update("Downloading entries",(size - downloaders.size())  + " from "+ size +" feeds are fetched", size - downloaders.size(), size, false);
                    if(downloaders.isEmpty()){
                        saveData();
                        Log.i("agcylog", "скачано "+Entries.list().size()+" статей за " + time);
                        Notificator.end();
                    }
                    else{
                        Log.i("agcylog", "начало загрузки фида " + downloaders.get(0).sourceId);
                        downloaders.get(0).start();
                    }
                }

                @Override
                public void onError(Loader.Error error) {

                }
            };
            entryLoader.addListener(entriesLoadedListener);

            downloaders.add(entryLoader);
        }



        if(!downloaders.isEmpty()){
            downloaders.get(0).start();
            Log.i("agcylog", "начало загрузки статьи");

            Notificator.show("Downloading entries","Wait a moment",0,100,false);
        }

    }


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

        @Override
        public Object chewData(String response) {
            Object chewerResponse = null;
            Log.i("agcylog","получен ответ загрузчика "+response);
            try{
                ArrayList<Feed> data = new ArrayList<Feed>();
                try{
                    data = new Gson().fromJson(response, new TypeToken<ArrayList<Feed>>(){}.getType());
                    chewerResponse = data;
                }
                catch (Exception exp){
                    Log.e("agcylog","ошибка сервера \\ парсера " + exp.getMessage());
                    chewerResponse = chewError(response);
                }
                Feeds.add(data);
                saveFeedsAsync(data);
            }catch(Exception exp){
                Log.e("agcylog","ошибка сохранения \\ обработки фидов "  + exp.getMessage());
            }
            return chewerResponse;
        }

        @Override
        public Error chewError(String error) {
            return null;
        }

    }
    public static class StreamLoader extends Loader{

        @Override
        public Object chewData(String response) {

            Object chewerResponse = null;
            Log.i("agcylog","получен ответ загрузчика "+response);

            try{
                Stream data = new Stream();
                try{
                    data = new Gson().fromJson(response, Stream.class);
                    chewerResponse = data;
                }catch(Exception exp){
                    Log.e("agcylog","ошибка сервера \\ парсера " + exp.getMessage());
                    chewerResponse = chewError(response);
                }

                if(sourceType==Stream.TYPE_FEED)
                    Feeds.get(sourceId).lastUpdate = System.currentTimeMillis();


                Entries.add(data.items);

            }catch (Exception exp){
                Log.e("agcylog","ошибка обработки стрима " + exp.getMessage());
            }

            return chewerResponse;
        }
        @Override
        public Error chewError(String error) {
            return null;
        }

        public static class Filter{
            public int count;
            public long newerThan;
            public boolean unreadOnly;

        }
        public Filter filter;
        public String sourceId;
        public int sourceType;
        public void setFilter(Filter filter){
            this.filter = filter;
            if(this.filter!=null){
                if(this.filter.unreadOnly)
                    parameters.put("unreadOnly", true);
                if(this.filter.count>0)
                    parameters.put("count", this.filter.count);
                if(this.filter.newerThan>0)
                    parameters.put("continuation",this.filter.newerThan);
            }
        }
        public StreamLoader(String sourceId,int sourceType,Filter filter) {
            super();
            this.sourceId = sourceId;
            this.sourceType = sourceType;
            setFilter(filter);
            methodName = "streams/contents";
            parameters.put("streamId",sourceId);


        }


    }
    public static class CategoryLoader extends Loader{

        public CategoryLoader() {
            super();
            baseUrl = "http://sandbox.feedly.com/v3/";
            methodName ="categories";
        }
        @Override
        public Object chewData(String response) {
            Object responseObject = null;

            Log.i("agcylog","получен ответ загрузчика "+response);
            try {
                ArrayList<Category> data = new ArrayList<Category>();
                try{
                    data = new Gson().fromJson(response, new TypeToken<ArrayList<Category>>() {}.getType());
                    responseObject = data;
                }catch (Exception exp){
                    Log.e("agcylog","ошибка сервера \\ парсера " + exp.getMessage());
                    responseObject = chewError(response);
                }
                Categories.add(data);
                saveCategoriesAsync(data);
            } catch (Exception exp) {
                Log.e("agcylog","ошибка обработки категории " + exp.getMessage());
            }
            return responseObject;
        }

        @Override
        public Error chewError(String error) {
            return null;
        }

    }
    public static class ReadMarker extends Loader{
        @Override
        public Object chewData(String response) {
            return null;
        }

        @Override
        public Error chewError(String error) {
            return null;
        }

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

    }

}
