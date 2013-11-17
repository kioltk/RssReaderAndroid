package com.agcy.reader.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

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

public class Feedler{
    public static SharedPreferences accessStorage;
     public static String url;
    private static String redirect_uri = "http://localhost";
    private static String client_id = "sandbox";
    private static String client_secret = "Z5ZSFRASVWCV3EFATRUY";
    private static String refresh_token;
    private static String access_token;

    public static void initialization(){
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

    public static void setAccess(SharedPreferences _accessStorage){
        accessStorage = _accessStorage;
        refresh_token  = accessStorage.getString("refresh_token", "");

    }
    public static void saveAccess(){
        SharedPreferences.Editor editor = accessStorage.edit();
        editor.putString("refresh_token", refresh_token);
        editor.apply();
    }
    public static void logout() {
        SharedPreferences.Editor editor = accessStorage.edit();
        editor.remove("refresh_token");
        editor.apply();
        accessStorage = null;
        Feeds.clear();
        Categories.clear();
        Entries.clear();
        refresh_token = null;
        access_token = null;
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

    public static class feedLoader extends Loader{
        public feedLoader(Context context) {
            super();
            baseUrl = "http://sandbox.feedly.com/v3/subscriptions";
        }
        public String data ="";
        public void chewData(){
            ArrayList<Feed> s = new Gson().fromJson(data, new TypeToken<ArrayList<Feed>>(){}.getType());
            Feeds.add(s);
        }
    }
    public static class entryLoader extends Loader{
        public String sourceId;
        public String sourceType;
        public entryLoader(String feedId) {
            sourceId = feedId;
            sourceType = "feed";
            baseUrl = "http://sandbox.feedly.com/v3/streams/contents?streamId="+sourceId;
        }
        public String data ="";
        public void chewData(){
           Stream s = new Gson().fromJson(data, Stream.class);
           Feed f =  Feeds.get(s.id);
                   f.entries.addAll(s.items);
            Entries.add(s.items);
        }
    }




}
