package com.agcy.reader.core;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by kiolt_000 on 01.11.13.
 */
public class Loader {
    static  String baseUrl = "http://atwar.blogs.nytimes.com/feed/";
    static String methodName = "";
    static AsyncHttpClient client = new AsyncHttpClient();
    private static void get(RequestParams params, AsyncHttpResponseHandler callback){

        client.get(baseUrl+methodName, params, callback);
    }
    public static void getFeed(AsyncHttpResponseHandler callback){


        methodName = "";
        RequestParams params = new RequestParams();

        get(params,callback);





    }
}
