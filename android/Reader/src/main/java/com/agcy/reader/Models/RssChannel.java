package com.agcy.reader.Models;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class RssChannel {
    public int lastPubDate;
    public String category;
    public String name;
    public String url;
    public String link;
    public String language;
    public String title;
    public ArrayList<RssItem> items;
    public void reloadItems(){
        items.clear();
        for(int i = 0; i<RssFeed.Items.size();i++){
            RssItem item = RssFeed.Items.get(i);
            if(item.channel.equals(name)){
                items.add(item);
            }
        }
    }
    public RssChannel(String name,String url,String category){
        items = new ArrayList<RssItem>();
        this.name = name;
        this.url = url;
    }
}
