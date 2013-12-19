package com.agcy.reader.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.agcy.reader.BackgroundActivity;
import com.agcy.reader.R;

import java.util.ArrayList;

/**
 * Created by kiolt_000 on 17.12.13.
 */
public class Notificator {
    static Context context;
    static NotificationManager notificationManager;
    static ArrayList<Notification> notifications;
    static Notification.Builder builder;

    public static void initialization(Context context){
        Notificator.context = context;
        notifications = new ArrayList<Notification>();
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context);
    }
    public static void show(String head, String body, int currentProgress,int maxProgress, boolean isPause,boolean isIndeterminated){


        builder = new Notification.Builder(context);
        builder.setContentTitle(head)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_whiteblack)
                .setProgress(currentProgress, maxProgress, isIndeterminated);

        Intent pauseResumeIntent = new Intent(context, BackgroundActivity.class);
        String buttonText = "";
        int buttonIc;
        if(!isPause){
            pauseResumeIntent.putExtra("command",BackgroundActivity.COMMAND_PAUSE);
            buttonText = "Pause";
            buttonIc = android.R.drawable.ic_media_pause;
        }
        else{
            pauseResumeIntent.putExtra("command",BackgroundActivity.COMMAND_RESUME);
            buttonText = "Resume";
            buttonIc = android.R.drawable.ic_media_play;
        }
        PendingIntent pauseResumePendingIntent = PendingIntent.getBroadcast(context, 0, pauseResumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        Intent cancelIntent = new Intent(context, BackgroundActivity.class);
        cancelIntent.putExtra("command",BackgroundActivity.COMMAND_CANCEL);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, 1, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setTicker(body);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN){
            builder.addAction(buttonIc, buttonText, pauseResumePendingIntent);
            builder.addAction(android.R.drawable.ic_delete, "Cancel", cancelPendingIntent);
        }else{
            builder.setContentIntent(pauseResumePendingIntent);

        }
        Notification n = builder.build();
        if(!isPause)
            n.flags |= Notification.FLAG_NO_CLEAR ;


        notificationManager.notify(0, n);
        notifications.add(n);

    }
    public static void update(String head, String status, int currentProgress,int maxProgress, boolean isIndeterminated){


        builder .setContentTitle(head)
                .setContentText(status)
                .setProgress(maxProgress,currentProgress,isIndeterminated)
                .setSmallIcon(R.drawable.ic_whiteblack);
       // mNotifyBuilder.setContentText("curr")
         //       .setNumber(1);
        Notification n = builder.build();
        n.flags |= Notification.FLAG_NO_CLEAR | Notification.PRIORITY_DEFAULT;


        notificationManager.notify(0, n);
    }
    public static void hide(){

        Notification n =   notifications.get(0);
        notificationManager.cancel(0);
    }

    public static void end() {
        builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_whiteblack)
                .setContentText("All downloads are finished")
                .setContentTitle("Done")
                .setTicker("All downloads are finished");

        Notification n = builder.build();
        n.flags |= Notification.PRIORITY_DEFAULT;


        notificationManager.notify(0, n);
        notifications.add(n);
    }
}
