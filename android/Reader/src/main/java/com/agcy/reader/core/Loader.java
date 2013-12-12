package com.agcy.reader.core;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by kiolt_000 on 01.11.13.
 */
public class Loader extends AsyncTask<String, Integer, String>  {
    String baseUrl = "http://sandbox.feedly.com/v3/profile";
    String methodName = "";
    String methodType = "GET";
    Object requestData;
    public void start(){
        execute("");
    }
    public void stop(){
        cancel(true);
    }
    @Override
    protected String doInBackground(String... params) {

        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer("");
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpRequestBase httpRequest = null;
            if(methodType.equals("GET")){
                httpRequest = new HttpGet();
            }
            if(methodType.equals("POST")){
                httpRequest = new HttpPost();
            }

            URI uri = new URI(baseUrl);
            httpRequest.setURI(uri);
            httpRequest.addHeader("Authorization", " OAuth "+Feedler.getToken());

            if(requestData!=null){
                httpRequest.addHeader("Content-type", "application/json");
                httpRequest.addHeader("Accept", "application/json");
                String json = new Gson().toJson(requestData);
                StringEntity se = new StringEntity(json);
                ((HttpPost)httpRequest).setEntity(se);
            }
            HttpResponse httpResponse = httpClient.execute(httpRequest);
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
            Log.e("agcylog",st);
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
