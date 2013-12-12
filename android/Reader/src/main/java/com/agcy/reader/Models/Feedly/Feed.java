package com.agcy.reader.Models.Feedly;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Feed {
    //public Feedler.EntryLoader downloader;
    public String id;
    public String title;
    public ArrayList<String> keywords;
    public int unreadCount(){
        int i = 0;
        for(Entry entry: entries.values()){
            if(entry.unread)
                i++ ;
        }
        return i;
    }
    public ArrayList<Entry> entries(){
        return new ArrayList<Entry>(entries.values());
    }
    private HashMap<String,Entry> entries;
    public String website;
    public float velocity;
    public Boolean featured;
    public Boolean sponsored;
    public Boolean curated;
    public int subscribers;
    public String state;
    public ArrayList<Category> categories;

    public String icon(){
        return "https://www.google.com/s2/favicons?domain="+ website;
    }
    public Feed(){
       this.entries = new HashMap<String,Entry>();
       this.categories = new ArrayList<Category>();
    }
    public void addEntry(Entry entry) {
        this.entries.put(entry.id,entry);
    }

    public String getCategories() {
        return new Gson().toJson(categories);
    }
    public void setCategories(String json){
        Type collectionType = new TypeToken<ArrayList<Category>>(){}.getType();
        categories = new Gson().fromJson(json, collectionType);
    }


}
