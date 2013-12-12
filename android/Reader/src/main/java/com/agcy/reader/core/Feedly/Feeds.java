package com.agcy.reader.core.Feedly;

import android.util.Log;

import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.Models.Feedly.Stream;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Feeds {

    private static HashMap<String,Feed> feeds;
    public static Feed get(String id){
       return feeds.get(id);
    }
    public static void add(Feed feed){
        feeds.put(feed.id, feed);
        Categories.chewFeed(feed);
    }
    public static void add(ArrayList<Feed> feedsList) {
        for(int i = 0; i< feedsList.size();i++){
            Feed feed = feedsList.get(i);
            add(feed);
        }
    }
    public static void initalization(){
        feeds = new HashMap<String, Feed>();
    }

    public static ArrayList<Feed> list() {

        return new ArrayList<Feed>(feeds.values());
    }

    public static void clear() {

        feeds = null;
    }

    public static void chewEntry(Entry entry) {
        Feed feed = Feeds.get(entry.origin.streamId);
        try{
        feed.addEntry(entry);
        }catch(Exception exp){
            Log.e("agcylog","не найден айди фида"+ entry.origin.streamId);
        }
    }

    public static Stream getStream(String sourceId) {
        Stream stream = new Stream();
        stream.id = sourceId;
        stream.items = get(sourceId).entries();
        return stream;
    }

    public static ArrayList<Feed> getByCategory(String id) {
        return null;
    }
}
