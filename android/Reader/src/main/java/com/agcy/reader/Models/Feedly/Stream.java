package com.agcy.reader.Models.Feedly;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Stream {
    public static final int TYPE_FEED = 0;
    public static final int TYPE_CATEGORY = 0;

    public String id;
    public ArrayList<Entry> items;
    public Stream(){
        items = new ArrayList<Entry>();
    }
}
