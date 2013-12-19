package com.agcy.reader.Models.Feedly;

import android.text.Html;

import com.agcy.reader.core.Feedly.Feeds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Entry {
    public String id;
    public Boolean unread;
    public Content summary;
    public Origin origin;
    public String content(){
        String summary = this.summary.content;
        if (summary==null){
            summary="no content";
        }
        String content = Html.fromHtml(summary).toString();
        content = content.replace("￼","");
        return content;
    }
    public String title;
    public long published;
    public String author;
    public String originId;
    public Visual visual;
    public Entry(){
        categories = new ArrayList<Category>();
        summary = new Content();
        origin = new Origin();
        visual = new Visual();
    }
    public ArrayList<Category> categories;
    public String dateLocale() {
        
        return new Date(published).toLocaleString();
    }
    public Date date(){
        return new Date(published);
    }


    public void setCategories(String json) {

        Type collectionType = new TypeToken<ArrayList<Category>>(){}.getType();
        categories = new Gson().fromJson(json, collectionType);
    }
    public Feed feed;
    public Feed feed(){
        if(feed==null){
           feed = Feeds.get(origin.streamId);
        }
        return feed;
    }


    public static class Visual{
        public String url;
        public int height;
        public int width;
        public String contentType;
        public Visual(){

        }
    }
    public static class Link{
        public String url;
        public Link(){

        }
    }
    public static class Origin{
        public String streamId;
        public String title;
        public String htmlUrl;
        public Origin(){

        }
    }
    public class Content{
        public String content;
    }

}
