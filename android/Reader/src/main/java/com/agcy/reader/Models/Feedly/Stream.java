package com.agcy.reader.Models.Feedly;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Stream {
    public String id;
    public ArrayList<Entry> items;
    public Stream(){
        items = new ArrayList<Entry>();
    }
}
