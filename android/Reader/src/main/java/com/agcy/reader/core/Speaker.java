package com.agcy.reader.core;

import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.agcy.reader.Models.Feedly.Entry;

import java.util.HashMap;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class Speaker  {
    private int MY_DATA_CHECK_CODE = 0;
    public static TextToSpeech myTTS;
    public static Entry currentEntry;

    public static Boolean isSpeaking(){
        return myTTS.isSpeaking();
    }
    public static void speak(Entry entry) {
        //speak straight away
        currentEntry = entry;
        if (myTTS.isSpeaking())
            stop();
        HashMap<String, String> params = new HashMap<String, String>();

        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");
        myTTS.speak(currentEntry.content(), TextToSpeech.QUEUE_FLUSH, params);
    }
    public static void stop(){
        myTTS.stop();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static void setProgressListener(UtteranceProgressListener progressListener) {
        myTTS.setOnUtteranceProgressListener(progressListener);
    }
}
