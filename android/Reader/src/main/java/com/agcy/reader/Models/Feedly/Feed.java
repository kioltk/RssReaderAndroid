package com.agcy.reader.Models.Feedly;

import com.agcy.reader.core.Feedly.Entries;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Feed {
    public String id;
    public String title;
    public ArrayList<String> keywords;
    public long lastUpdate; // mine

    public int unreadCount(){
        int i = 0;
        for(Entry entry: entries()){
            if(entry.unread)
                i++ ;
        }
        return i;
    }

    public ArrayList<Entry> entries(){
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (Entry entry: Entries.list()){
            if(entry.feed()==this)
                entries.add(entry);
        }
        return entries;
    }
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
       this.categories = new ArrayList<Category>();
    }
    public void addEntry(Entry entry) {
    //    this.entries.add(entry);
    }

    public String getCategories() {
        return new Gson().toJson(categories);
    }
    public void setCategories(String json){
        Type collectionType = new TypeToken<ArrayList<Category>>(){}.getType();
        categories = new Gson().fromJson(json, collectionType);
    }


}
