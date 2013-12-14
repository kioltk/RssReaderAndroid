package com.agcy.reader.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.R;
import com.agcy.reader.core.Feedly.Entries;
import com.agcy.reader.core.Feedly.Feeds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kiolt_000 on 10.12.13.
 */
public class Imager {
    public static HashMap<String, Boolean> imageStoraged;
    public static HashMap<String,Bitmap> imageCache;
    static HashMap<String,DownloadImageTask> imageloadingTasks;
    static int imageLoader = 0;
    static Context context;

    // todo: сохранение на сд карту

    public static void initialization(Context context) {
        imageStoraged = new HashMap<String, Boolean>();
        imageCache = new HashMap<String, Bitmap>();
        imageloadingTasks = new HashMap<String, DownloadImageTask>();
        Imager.context = context;
        checkStoraged();
        SaveImageTask.path = context.getExternalCacheDir();
    }


    public static void downloadImages(){

        String coded =  codeFilename("http://www.mkyong.com/java/how-do-convert-byte-array-to-string-in-java/");
        String decoded = decodeFilename(coded);
        ArrayList<String> requiredImages = new ArrayList<String>();
        for(Feed feed: Feeds.list()){
            String url = feed.icon();
            if(get(url)==null  && !isStoraged(url)){
                if(!requiredImages.contains(url))
                    requiredImages.add(url);
            }


        }

        Log.i("agcylog","забиты все фиды на закачку");

        for(Entry entry: Entries.list()){
            String url= entry.visual.url;
            if(url!=null)
                if(get(url)==null && !isStoraged(url))
                    if(!requiredImages.contains(url))
                        requiredImages.add(url);

        }
        for(final String imageUrl:requiredImages){
            DownloadImageTask task = imageLoader(imageUrl);

            task.addListener(new Listener() {
                @Override
                public void onEnd(Object object) {

                    saveImage(imageUrl);
                    if(!imageloadingTasks.isEmpty()){

                        Log.i("agcylog","осталось закачек" + imageloadingTasks.size());
                        firstLoader().start();
                    }
                    else{
                        Log.i("agcylog","Закачки картинок завершены");
                    }
                }
            });
            imageloadingTasks.put(imageUrl, task);
        }
        Log.i("agcylog","забиты все картинки на закачку");
        if(!imageloadingTasks.isEmpty()){

            Log.i("agcylog","осталось закачек" + imageloadingTasks.size());
            DownloadImageTask task = firstLoader();
            task.start();
        }
    }

    private static boolean isStoraged(String url) {
            return imageStoraged.get(url)!=null;
    }
    public static void checkStoraged(){

        File path = context.getExternalCacheDir();
        File imageList[] = path.listFiles();
        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        for(File file:imageList){

            String fileName = decodeFilename(file.getName());
            imageStoraged.put(fileName,true);
        }
    }
    public static void saveImage(String url){
        try {

            String filename = codeFilename(url);
            File path = Environment.getDataDirectory(),
                    path2 = Environment.getDownloadCacheDirectory(),
                    path3 = Environment.getExternalStorageDirectory(),
                    path4 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    path5 = Environment.getRootDirectory(),
                    path6 = context.getFilesDir(),
                    path7 = context.getExternalCacheDir();
            Bitmap bmp = imageCache.get(url);
            if(bmp!=null)
                try{

                    /*
                        OutputStream fOut = null;
                        File file = new File(path7, filename);
                        fOut = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        Log.i("agcylog","изображение сохранено на диск " +url );
                        imageCache.remove(url);
                        imageStoraged.put(url, true);
                    */
                    new SaveImageTask(url).execute();
                }catch (Exception exp){
                    Log.e("agcylog","ошибка сохранения  на диск " + exp.getMessage());
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadImage(String url){

        if(imageStoraged.containsKey(url)){

            File path7 = context.getExternalCacheDir();
            String filename = codeFilename(url);
            File file = new File(path7+"/"+filename);

            if(file!=null){
                Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageCache.put(url,b);
            }
        }/**/
    }
    private static String codeFilename(String fileName){
        byte[] bytes = fileName.getBytes();
        String coded = "";
        coded = String.format("%040x", new BigInteger(1, fileName.getBytes()));
        return coded;
    }
    private static String decodeFilename(String codedFileName){

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < codedFileName.length(); i+=2) {
            String str = codedFileName.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        String decoded = output.toString();
        return decoded;
    }


    public static DownloadImageTask imageLoader(String url){
        return new DownloadImageTask(url);
    }
    public static DownloadImageTask firstLoader(){
        if(!imageloadingTasks.isEmpty()){
            Object[] array =  imageloadingTasks.values().toArray();
            return (DownloadImageTask) array[0];
        }
        return null;
    }


    public static Bitmap get(String url) {


        Bitmap bitmap = imageCache.get(url);
        if(bitmap==null)
           if(isStoraged(url)){
              loadImage(url);
              bitmap = imageCache.get(url);
           }
        return bitmap;
    }
    public static void setImage(String url, final ImageView imageView) {

        Bitmap bitmap = get(url);
        if( bitmap == null ){
            try{
                DownloadImageTask task;
                task = imageloadingTasks.get(url);
                if(task==null){
                    task = new DownloadImageTask(url);
                    imageloadingTasks.put(url,task);
                    task.start();
                }
                task.addListener(new Listener() {
                    @Override
                    public void onEnd(Object object) {
                        if(object!=null){
                            imageView.setImageBitmap((Bitmap) object);

                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }else
                            imageView.setImageDrawable(context.getResources().getDrawable( R.drawable.ic_launcher));
                        imageView.clearAnimation();

                    }
                });
                Animation a = AnimationUtils.loadAnimation(context, R.anim.rotating);
                a.setDuration(1000);
                imageView.startAnimation(a);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.loading));
            }catch (Exception exp){
                Log.e("agcylog","хрня какая-то");
            }
        }else{
            imageView.setImageBitmap(bitmap);
        }

    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        String urldisplay;

        public DownloadImageTask(String url) {
            this.urldisplay = url;
            listeners = new ArrayList<Listener>();
        }

        public DownloadImageTask() {

            listeners = new ArrayList<Listener>();
        }

        protected Bitmap doInBackground(String... urls) {
            if(urldisplay==null)
                urldisplay = urls[0];
            Log.i("agcylog","начало загрузки картинки " + urldisplay);
            Bitmap mIcon11 = null;
            try {

                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

                Log.i("agcylog","загружена картинка " + urldisplay);
            } catch (Exception e) {
                Log.e("agcylog", "ошибка загрузки " + e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result!=null)
                imageCache.put(urldisplay, result);

            DownloadImageTask task = imageloadingTasks.remove(urldisplay);
            if(task==null)
                Log.i("agcylog","таска не вытянута(");
            for(Listener listener:listeners){
                listener.onEnd(result);
            }

        }
        public void addListener(Listener listener){
            listeners.add(listener);
        }
        public ArrayList<Listener> listeners;

        public void start() {
            if(getStatus()==Status.PENDING)
                execute();
            else
                Log.e("agcylog","Ошибка алгоритма - таска должна быть остановлена");
        }
    }
    public static abstract class Listener{
        public abstract void onEnd(Object object);
    }
    public static class SaveImageTask extends  AsyncTask<String,Void,Boolean>{
        static File path;
        Bitmap file;
        String url;
        public SaveImageTask(String url){
            this.url = url;
            this.file = imageCache.get(url);
        }
        @Override
        protected Boolean doInBackground(String... params) {
            String filename = codeFilename(url);
            try{
                OutputStream fOut = null;
                File file = new File(path, filename);
                fOut = new FileOutputStream(file);
                this.file.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                Log.i("agcylog","изображение сохранено на диск " +url );
                imageCache.remove(url);
                imageStoraged.put(url, true);
            }catch (Exception exp){
                Log.e("agcylog","изображение не сохранено " + exp.getMessage());
            }
            return null;
        }
    }
}
