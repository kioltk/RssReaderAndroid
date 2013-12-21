package com.agcy.reader.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 30.11.13.
 */
public class Storage extends SQLiteOpenHelper {

    static final String TABLE_ENTRIES = "entry";
    static final String TABLE_FEEDS = "feed";
    static final String TABLE_CATEGORIES = "category";
    static final String TABLE_FEED_CATEGORIES = "feedCategories";
    static final String KEY_ID = "id";
    static final String CREATE_TABLE_ENTRIES = " CREATE TABLE "
            + TABLE_ENTRIES + " ( " + KEY_ID + " TEXT PRIMARY KEY, "
            + " content " + " TEXT, "
            + " streamId " + " TEXT, "
            + " title" + " TEXT, "
            + " image " + " TEXT, "
            + " date " + " INTEGER, "
            + " unread " +" INTEGER " + ")";
    static final String CREATE_TABLE_FEEDS = "CREATE TABLE "
            + TABLE_FEEDS + "(" + KEY_ID + " TEXT PRIMARY KEY,"
            + " title " + " TEXT, "
            + " website " + " text, "
            + " lastupdate " + " integer "
            +")";
    static final String CREATE_TABLE_CATEGORIES = " CREATE TABLE "
            + TABLE_CATEGORIES + " ( " + KEY_ID + " TEXT PRIMARY KEY,"
            + " label " + " TEXT " + " ) ";
    static final String CREATE_TABLE_FEED_CATEGORIES = " CREATE TABLE "
            + TABLE_FEED_CATEGORIES + " ( FEEDID TEXT ,"
            + " CATEGORYID TEXT,"
            + " id TEXT PRIMARY KEY" + " ) ";
    String DB_NAME = "";
    Context mContext;
    public static Storage _storage;
    public Storage(Context context, String db_name){
        super(context, db_name, null, 1);
        DB_NAME = db_name;
        mContext = context;
    }
    public static void initialization(Context context){
        _storage = new Storage(context,"feedly");
        savers = new ArrayList<Saver>();
    }
    public static void startSavers(){

        for(Saver saver:savers){
            saver.start();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ENTRIES);
        db.execSQL(CREATE_TABLE_FEEDS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_FEED_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + DB_NAME);
        onCreate(db);
    }

    public static void save(String contentType, ContentValues values) {

        SQLiteDatabase db = _storage.getWritableDatabase();
        try {

            Log.i("agcylog","сохраняем "+contentType);
            if(db.insert(contentType, null , values)==-1){
               db.update(contentType,values," id = '"+values.get("id")+"'",null);
            }
            Log.i("agcylog","сохранено "+contentType);
        }catch (Exception ignored){
            Log.i("agcylog","error"+ignored.getMessage());
        }


    }
    public static Cursor get(String table) {
        SQLiteDatabase db = _storage.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + table;

        Cursor c = null;
        if (db != null) {
            c = db.rawQuery(selectQuery, null);
        }

        return c;
    }
    public static void end() {
        SQLiteDatabase db = _storage.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();

    }
    public static ArrayList<Saver> savers;
    public static abstract class Saver{
        public abstract void toSave();
        public int status = 0;
        public void start() {
            final Object me = this;
            if(status==0){
                status = 1;
                new AsyncTask<String, String, Void>(){

                    @Override
                    protected Void doInBackground(String... params) {
                        Log.i("agcylog","Начало сохранения ");
                        toSave();
                        savers.remove(me);
                        status = 2;

                        Log.i("agcylog","сохранено ");
                        return null;
                    }
                    }.execute();
            }
        }
    }
}
