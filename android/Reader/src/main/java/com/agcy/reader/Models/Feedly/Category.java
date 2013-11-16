package com.agcy.reader.Models.Feedly;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Category {
    public String id;
    public String label;
    public ArrayList<Subcription> subcriptions;
    public Category(){
        subcriptions = new ArrayList<Subcription>();
    }
}
