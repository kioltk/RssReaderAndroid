package com.agcy.reader.Models.Feedly;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Subcription {
    public String id;
    public String title;
    public ArrayList<Category> categories;
    public ArrayList<Feed> feeds;
    public String sortid;
    public int updated;
    public String website;
    public Subcription(){
        categories = new ArrayList<Category>();
        feeds = new ArrayList<Feed>();
    }

}
