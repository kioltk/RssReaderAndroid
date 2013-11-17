package com.agcy.reader.Models.Feedly;

import android.text.Html;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Entry {
    public String id;
    public Boolean unread;
    public Content summary;
    public String content(){
        String content = Html.fromHtml(summary.content).toString();
        content = content.replace("￼","");
        return content;
    }
    public String title;
    public long published;
    public String author;
    public String originId;
    public Visual visual;
    public ArrayList<Category> categories;
    public Entry(){

    }

    public static class Visual{
        public String url;
        public int height;
        public int width;
        public String contentType;
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
        //public String direction;
        public String content;
    }

}
