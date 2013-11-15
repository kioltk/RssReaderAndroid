package com.agcy.reader.Models;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class RssCategory {
    public String name;
    public ArrayList<RssChannel> Items;
    public void reloadItems(){
        Items.clear();
        for(int i = 0; i<RssFeed.Items.size();i++){
            RssChannel item = RssFeed.Channels.get(i);
            if(item.category.equals(name)){
                Items.add(item);
            }
        }
    }
    public RssCategory(String name){
        this.name = name;
        Items = new ArrayList<RssChannel>();
    }
}
