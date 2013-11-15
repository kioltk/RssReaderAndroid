package com.agcy.reader.core;

import android.speech.tts.TextToSpeech;

import java.util.HashMap;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class Speaker  {
    private int MY_DATA_CHECK_CODE = 0;
    public static TextToSpeech myTTS;
    public static void speak(String content) {
        //speak straight away
        if (myTTS.isSpeaking())
            stop();
        HashMap<String, String> params = new HashMap<String, String>();

        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");
        myTTS.speak(content, TextToSpeech.QUEUE_FLUSH, params);
    }
    public static void stop(){
        myTTS.stop();
    }

}
