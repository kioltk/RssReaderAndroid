package com.agcy.reader.Models;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class RssItem {
    public String channel = "";
    public String description = "";
    public String title = "";
    public String link = "";
    public String pubDate = "";
    public String author = "";
    public String language = "";
    public String image = "";
    public RssItem(){
    }
    @Override
    public String toString() {
        return  title + description;
    }
}
