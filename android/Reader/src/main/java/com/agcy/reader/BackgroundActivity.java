package com.agcy.reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.agcy.reader.core.Imager;

/**
 * Created by kiolt_000 on 17.12.13.
 */
public class BackgroundActivity extends BroadcastReceiver {
    public static String currentTask ="";
    public static final int COMMAND_PAUSE = 0;
    public static final int COMMAND_RESUME = 1;
    public static final int COMMAND_CANCEL = 2;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        int command;
        try{
            command =  bundle.getInt("command");
        }catch (Exception exp){
            command = intent.getIntExtra("command",-1);
        }
        switch (command){
            case COMMAND_CANCEL:
                cancelLoading();
                break;
            case COMMAND_PAUSE:
                pauseLoading();
                break;
            case COMMAND_RESUME:
                resumeLoading();
                break;
        }
    }
    private void cancelLoading() {
        Imager.cancelDownloading();
    }
    private void resumeLoading() {
        Imager.resumeDownloading();
    }
    public static void pauseLoading(){
        Imager.pauseDownloading();
    }

}
