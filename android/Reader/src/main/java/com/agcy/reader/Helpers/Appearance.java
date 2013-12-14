package com.agcy.reader.Helpers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by kiolt_000 on 10.12.13.
 */
public class Appearance {
    public static void slideFromBottom(View view){
        view.setAlpha(0);
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationY", 400, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(50);
        set.setDuration(400);
        set.start();
    }
    public static void slideFromTop(View view){
        view.setAlpha(0);
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationY", -400, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(50);
        set.setDuration(400);
        set.start();
    }
}
