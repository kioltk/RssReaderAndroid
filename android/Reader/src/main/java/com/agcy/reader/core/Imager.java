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
import android.widget.Toast;

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
import java.util.Map;

/**
 * Created by kiolt_000 on 10.12.13.
 */
public class Imager {
    public static HashMap<String, Boolean> imageStoraged;
    public static HashMap<String,Bitmap> imageCache;
    static HashMap<String,DownloadImageTask> imageloadingTasks;
    static ArrayList<String> requiredImages;
    static int imageLoader = 0;
    static Context context;
    public static final int STATUS_NONE = 0;
    public static final int STATUS_DONE = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_PAUSE = 3;
    public static final int STATUS_FETCHING = 4;



    public static int status = STATUS_NONE;

    // todo: сохранение на сд карту

    public static void initialization(Context context) {
        imageStoraged = new HashMap<String, Boolean>();
        imageCache = new HashMap<String, Bitmap>();
        imageloadingTasks = new HashMap<String, DownloadImageTask>();
        Imager.context = context;
        checkStoraged();
        SaveImageTask.path = context.getExternalCacheDir();
    }
    static FetchImagesTask fetchImagesTask;
    public static void downloadImages(){

        if(status==STATUS_NONE){
            fetchImagesTask= new FetchImagesTask();
            fetchImagesTask.execute();
            Notificator.show("Image loading","Fetching...",0,0,false,true);
            status =STATUS_FETCHING;
        }

    }

    public static int tasksCount() {
        return imageloadingTasks.size();
    }

    private static class FetchImagesTask extends AsyncTask<String,String,String>{
        @Override
        public String doInBackground(String... params)
        {
            requiredImages = new ArrayList<String>();
            for(Feed feed: Feeds.list()){
                String url = feed.icon();
                if(!isCached(url)  && !isStoraged(url)){
                    if(!requiredImages.contains(url))
                        requiredImages.add(url);
                }


            }

            Log.i("agcylog","забиты все фиды на закачку");
            for(Entry entry: Entries.list()){
                String url= entry.visual.url;
                if(url!=null)
                    if(!isCached(url) && !isStoraged(url))
                        if(!requiredImages.contains(url))
                            requiredImages.add(url);
                if(entry.summary.content!=null){
                    ArrayList<String> imagesFromEntry = Parser.fetchImages(entry.summary.content);
                    for(String imageFromEntry:imagesFromEntry){
                        if(!isCached(imageFromEntry) && !isStoraged(imageFromEntry))
                            if(!requiredImages.contains(imageFromEntry))
                                requiredImages.add(imageFromEntry);
                    }
                }
            }
            Log.i("agcylog","картинки со всех статей забиты на закачку");
            for(final String imageUrl:requiredImages){
                DownloadImageTask task = imageloadingTasks.get(imageUrl);
                if(task==null){
                    task = imageLoader(imageUrl);
                    imageloadingTasks.put(imageUrl, task);
                }
                task.addListener(new Listener() {
                    @Override
                    public void onEnd(Object object) {

                        saveImage(imageUrl);
                        if (!imageloadingTasks.isEmpty()) {
                            Toast.makeText(context, "осталось закачек" + imageloadingTasks.size(), Toast.LENGTH_SHORT).show();

                            Log.i("agcylog", "осталось закачек" + imageloadingTasks.size());
                            firstLoader().start();
                            showNotifyUpdate();
                        } else {
                            Log.i("agcylog", "Закачки картинок завершены");
                        }
                    }
                });
            }

            Log.i("agcylog","забиты все картинки на закачку");
            if(!imageloadingTasks.isEmpty()){

                showNotifyUpdate();
                Log.i("agcylog","осталось закачек" + imageloadingTasks.size());
                DownloadImageTask task = firstLoader();
                task.start();
                status = STATUS_LOADING;
            }else {
                Notificator.end();
                status = STATUS_DONE;
            }
            return "ok";
        }
    }
    private static void showNotifyUpdate(){
        Notificator.update("Image loading",
                requiredImages.size()-imageloadingTasks.size()+" of "+ requiredImages.size(),
                requiredImages.size()-imageloadingTasks.size(),
                requiredImages.size(),false);
    }
    private static boolean isStoraged(String url) {
        return imageStoraged.get(url)!=null;
    }
    private static boolean isCached(String url){
        return imageCache.get(url)!=null;
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

    public static void resumeDownloading(){
        if(status==STATUS_NONE)
            downloadImages();
        else{
            if(status==STATUS_PAUSE){
                firstLoader().execute();
                status = STATUS_LOADING;
                Notificator.show("Image loading",
                        "",
                        0,
                        100,
                        false,
                        false);
                showNotifyUpdate();
            }
        }
    }
    public static void pauseDownloading(){
        if(status==STATUS_FETCHING){
            status = STATUS_NONE;
            fetchImagesTask.cancel(true);
        }else {
            final DownloadImageTask task = firstLoader();
            task.cancel(true);
            status = STATUS_PAUSE;
            for(Map.Entry<String,DownloadImageTask> entry:imageloadingTasks.entrySet()){
                if(entry.getValue()==task){
                    final String urldisplay = task.urldisplay;
                    DownloadImageTask task1 = new DownloadImageTask(urldisplay);
                    task1.addListener(new Listener() {
                        @Override
                        public void onEnd(Object object) {

                            saveImage(urldisplay);
                            if (!imageloadingTasks.isEmpty()) {
                                Toast.makeText(context, "осталось закачек" + imageloadingTasks.size(), Toast.LENGTH_SHORT).show();
                                showNotifyUpdate();
                                Log.i("agcylog", "осталось закачек" + imageloadingTasks.size());
                                firstLoader().start();
                            } else {
                                Notificator.end();
                                Log.i("agcylog", "Закачки картинок завершены");
                            }
                        }
                    });
                    imageloadingTasks.put(entry.getKey(),task1);
                }
            }
        }
        Notificator.show("Image loading",
                "Paused",
                0,
                0,
                true,
                false);
    }
    public static void cancelDownloading(){
        if(status==STATUS_FETCHING){
            fetchImagesTask.cancel(true);
            fetchImagesTask=null;
        }
            final DownloadImageTask task = firstLoader();
            if(task!=null)
                task.cancel(true);

            imageloadingTasks.clear();

        Notificator.hide();
        status =STATUS_NONE;
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
    public static void setImageUrl(String url, final ImageView imageView) {

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
                final ImageView.ScaleType scaleType = imageView.getScaleType();
                task.addListener(new Listener() {
                    @Override
                    public void onEnd(Object object) {
                        if(object!=null){
                            imageView.setScaleType(scaleType);
                            setImageBitmap((Bitmap) object, imageView);
                        }else
                            imageView.setImageDrawable(context.getResources().getDrawable( android.R.drawable.ic_delete));
                        imageView.clearAnimation();

                    }
                });
                setImageLoading(imageView);
            }catch (Exception exp){
                Log.e("agcylog","хрня какая-то");
            }
        }else{
            imageView.setImageBitmap(bitmap);
        }

    }
    public static void setImageBitmap(Bitmap object, ImageView imageView) {
        imageView.setImageBitmap((Bitmap) object);
    }
    public static void setImageLoading(ImageView imageView) {
        //ViewGroup.LayoutParams params = imageView.getLayoutParams();
        //params.height = 100;
        //imageView.setLayoutParams(params);
        Animation a = AnimationUtils.loadAnimation(context, R.anim.rotating);
        a.setDuration(1000);

        imageView.startAnimation(a);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //imageView.setBackground(context.getResources().getDrawable(R.drawable.background));
        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.loading));
    }
    public static void setImageGif(ImageView imageView, Object Gif) {
        return;
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
            if (urldisplay==null)
                return null;
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
    public static abstract class Listener{
        public abstract void onEnd(Object object);
    }
}
