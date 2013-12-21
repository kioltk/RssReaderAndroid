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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiolt_000 on 01.11.13.
 */
public abstract class Loader extends AsyncTask<String, Integer, String>  {
    String baseUrl;
    String methodName;
    HashMap<String,Object> parameters;
    String methodType;
    Object requestData;
    public String parametersConverter(){
        String stringedParameters = "";
        for(Map.Entry<String, Object> parameter:parameters.entrySet()){
            stringedParameters +=
                    parameter.getKey()+"="+
                            parameter.getValue().toString().replace("?", "%3f")
                                    .replace("#","%23")
                                    .replace("&","%26")+"&";
        }
        return stringedParameters;
    }
    public void start() {
        if(getStatus()==Status.PENDING){

            execute();
        }
        else
            Log.e("agcylog","Ошибка алгоритма - таска должна быть остановлена");
    }
    public void stop(){
        cancel(true);
    }
    public Loader(){
        parameters = new HashMap<String, Object>();
        listeners = new ArrayList<onEndListener>();
        baseUrl = "http://sandbox.feedly.com/v3/";
        methodType = "GET";
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

            String convertedUrl = baseUrl+methodName;
            if (!parameters.isEmpty()){
                convertedUrl+="?" + parametersConverter();
            }
            URI uri = new URI(convertedUrl);
            httpRequest.setURI(uri);
            httpRequest.addHeader("Authorization", " OAuth "+Feedler.getToken());

            if(requestData!=null && methodType.equals("POST")){
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
    @Override
    public void onPostExecute(String result) {
        Object object = chewData(result);
        if (!(object instanceof Error))
            for(onEndListener listener:listeners){
                listener.toDo(object);
            }
        else
            for(onEndListener listener:listeners){
                listener.onError((Error) object);
            }
    }
    public void addListener(onEndListener listener){
        listeners.add(listener);
    }
    public ArrayList<onEndListener> listeners;
    public abstract Object chewData(String response);
    public abstract Error chewError(String error);
    public static abstract class onEndListener{
        public abstract void toDo(Object response);
        public abstract void onError(Error error);
    }
    public static class Error{
        public String errorId;
        public String errorMessage;
    }
}
