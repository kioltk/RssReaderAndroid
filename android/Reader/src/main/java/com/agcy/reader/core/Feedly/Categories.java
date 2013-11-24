package com.agcy.reader.core.Feedly;

import com.agcy.reader.Models.Feedly.Category;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Categories {

    private static HashMap<String,Category> categories;
    public static Category get(String id){
        return categories.get(id);
    }
    public static void add(Category category){
        categories.put(category.id,category);
    }
    public static void add(ArrayList<Category> categoriesList) {
        for(int i = 0; i< categoriesList.size();i++){
            Category feed = categoriesList.get(i);
            categories.put(feed.id, feed);
        }
    }
    public static void initalization(){
        categories = new HashMap<String, Category>();
    }

    public static void clear() {

        categories = null;
    }
}
