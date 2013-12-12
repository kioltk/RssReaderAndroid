package com.agcy.reader.Models.Feedly;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Category {
    public String id;
    public String label;
    public int unreadCount(){
        int i = 0;
        for(Entry entry: entries()){
            if(entry.unread)
                i++ ;
        }
        return i;
    }
    public ArrayList<Feed> feeds(){
        return new ArrayList<Feed>(feeds.values());
    }
    public ArrayList<Entry> entries(){
        ArrayList<Entry> arrayList = new ArrayList<Entry>();
        for(Feed feed:feeds()){
            arrayList.addAll(feed.entries());
        }
        return arrayList;
    }
    private HashMap<String,Feed> feeds;
    public Category(){
        feeds = new HashMap<String, Feed>();

    }
    public void addFeed(Feed feed) {
        feeds.put(feed.id, feed);
    }
    public void addFeeds(ArrayList<Feed> feeds){
        for(Feed feed:feeds){
            addFeed(feed);
        }
    }


}
