package com.agcy.reader.Models.Feedly;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Feed {
    public String id;
    public String title;
    public ArrayList<String> keywords;
    public ArrayList<Entry> entries;
    public String website;
    public float velocity;
    public Boolean featured;
    public Boolean sponsored;
    public Boolean curated;
    public int subscribers;
    public String state;
    public Feed(){
       entries = new ArrayList<Entry>();
    }


}
