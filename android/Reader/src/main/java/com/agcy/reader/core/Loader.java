package com.agcy.reader.core;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
    static  String baseUrl = "http://sandbox.feedly.com/v3/profile";
    static String methodName = "";

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
            HttpGet httpGet = new HttpGet();

            URI uri = new URI(baseUrl);
            httpGet.setURI(uri);
            httpGet.addHeader("Authorization", " OAuth "+Feedler.getToken());

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
