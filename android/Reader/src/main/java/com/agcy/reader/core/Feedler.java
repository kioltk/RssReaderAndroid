package com.agcy.reader.core;

import android.os.StrictMode;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class Feedler{

    public static String url;
    private static String redirect_uri = "http://localhost";
    private  static String client_id = "sandbox";
    private static String client_secret = "Z5ZSFRASVWCV3EFATRUY";
    private static String refresh_token;
    private static String access_token;
    public static void startLogin(){
        url ="http://sandbox.feedly.com/v3/auth/auth?response_type=code";


        url = url +
                "&client_id="+client_id +
                "&redirect_uri="+redirect_uri+
                "&scope=https://cloud.feedly.com/subscriptions";
    }
    public static String code;

    public static void endLogin(String requestCode) {
        String grant_type;
        url ="http://sandbox.feedly.com/v3/auth/token?";
        code = requestCode;
        grant_type = "authorization_code";
        url = url +
                "code="+ code +
                "&client_id="+client_id+
                "&client_secret="+client_secret+
                "&redirect_uri="+redirect_uri+
                "&grant_type="+grant_type;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(){
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                LoginResponse lr = new Gson().fromJson(response,LoginResponse.class);
                refresh_token = lr.refresh_token;
                access_token = lr.access_token;
                String st = getFeed();
            }

            @Override
            public void onFailure(Throwable e, String response) {
            }

            @Override
            public void onFinish() {

            }
        };

        client.post(url, handler);
    }
    public static void refreshLogin() {
        String grant_type;
        url ="http://sandbox.feedly.com/v3/auth/token?";
        grant_type = "refresh_token";
        url = url +
                "refresh_token="+ refresh_token +
                "&client_id="+client_id+
                "&client_secret="+client_secret+
                "&grant_type="+grant_type;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(){
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(String response) {
                LoginResponse lr = new Gson().fromJson(response,LoginResponse.class);
                access_token = lr.access_token;
            }

            @Override
            public void onFailure(Throwable e, String response) {
            }

            @Override
            public void onFinish() {

            }
        };

        client.post(url, handler);
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





    public static String getFeed(){
        //curl -u user:password http://sample.campfirenow.com/rooms.xml
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI("http://sandbox.feedly.com/v3/profile");
            httpGet.setURI(uri);
            httpGet.addHeader("Authorization", " OAuth "+access_token);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            String st = e.getLocalizedMessage();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return stringBuffer.toString();

    }



}
