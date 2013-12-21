package com.agcy.reader.core.Feedly;

import com.agcy.reader.Models.Feedly.Category;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.Models.Feedly.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kiolt_000 on 16.11.13.
 */
public class Categories {

    private static HashMap<String,Category> categories = new HashMap<String, Category>();
    static Category uncategoried = new Category(){{id = "uncategoried";label = "Uncategoried";}};
    static Category all  = new Category(){{id = "all";label="All";}};
    public static Category getAll(){
        return all;
    }
    public static Category getUncategoried(){
        return uncategoried;
    }
    public static Category get(String id){
        return categories.get(id);
    }
    public static void add(Category category){
        categories.put(category.id,category);
    }
    public static void add(ArrayList<Category> categoriesList) {
        for (Category category : categoriesList) {
            categories.put(category.id, category);
        }
    }

    public static List<Category> list() {
        ArrayList<Category> categoriesList = new ArrayList<Category>();
        categoriesList.add(getAll());
        categoriesList.addAll(categories.values());
        categoriesList.add(uncategoried);
        return categoriesList;
    }

    public static ArrayList<Category> getCategories(){
        return new ArrayList<Category>(categories.values());
    }

    public static void clear() {

        categories = null;
    }

    public static void chewFeed(Feed feed) {
        if(!feed.categories.isEmpty())
            for(Category categoryTemp:feed.categories){
                get(categoryTemp.id).addFeed(feed);
            }
        else{
            uncategoried.addFeed(feed);
        }
        all.addFeed(feed);

    }

    public static Stream getStream(String sourceId) {
        Stream stream = new Stream();
        stream.id = sourceId;
        if(sourceId.equals("all")){
            stream.items = all.entries();
        }else{
            if(sourceId.equals("uncategoried")){
                stream.items = uncategoried.entries();
            }else
                stream.items = get(sourceId).entries();
        }
        return stream;
    }


}
