package com.agcy.reader.core.Feedly;

import com.agcy.reader.Models.Feedly.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Entries {

    private static HashMap<String,Entry> entries;
    public static Entry get(String id){
        return entries.get(id);
    }
    public static void add(Entry entry){
        entries.put(entry.id, entry);
        Feeds.chewEntry(entry);
    }
    public static void initalization(){
        entries = new HashMap<String, Entry>();
    }

    public static void add(ArrayList<Entry> entriesList) {
        for (Entry entry : entriesList) {
            add(entry);
        }
    }
    public static List<Entry> list() {

        return new ArrayList<Entry>(entries.values());
    }

    public static void clear() {

        entries = null;
    }

    public static void markAsRead(String id) {

    }
}
