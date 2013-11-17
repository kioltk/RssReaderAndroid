package com.agcy.reader.core.Feedly;

import com.agcy.reader.Models.Feedly.Feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    }
    public static void add(ArrayList<Feed> feedsList) {
        for(int i = 0; i< feedsList.size();i++){
            Feed feed = feedsList.get(i);
            feeds.put(feed.id, feed);
        }
    }
    public static void initalization(){
        feeds = new HashMap<String, Feed>();
    }

    public static List<Feed> list() {

        return new ArrayList<Feed>(feeds.values());
    }


    public static void clear() {

        feeds =null;
    }
}
