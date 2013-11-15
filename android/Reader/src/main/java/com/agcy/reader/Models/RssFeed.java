package com.agcy.reader.Models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class RssFeed {
    public static HashMap<String,RssChannel> Channels;
    public static HashMap<String,ArrayList<HashMap>> Category;
    public static ArrayList<RssItem> Items;
    public static void reload(){
        Items = new ArrayList<RssItem>();
    }
}
