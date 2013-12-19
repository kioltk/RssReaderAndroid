package com.agcy.reader.Helpers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by kiolt_000 on 10.12.13.
 */
public class Appearance {
    public static Animator slideFromBottom(View view){
        view.setAlpha(0);
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationY", 400, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(150);
        set.setDuration(400);
        set.start();
        return set;
    }
    public static Animator slideFromTop(View view){
        view.setAlpha(0);
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationY", -400, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(150);
        set.setDuration(400);
        set.start();
        return set;
    }
    public static Animator slideToTop(View view){
        view.setAlpha(0);
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationY", 0, -400);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(50);
        set.setDuration(400);
        set.start();
        return set;
    }
    public static Animator slideFromLeft(View view){

        int width = view.getWidth();
        view.setAlpha(0);
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationX", -width, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(50);
        set.setDuration(400);
        set.start();
        return set;
    }
    public static Animator slideFromRight(View view){

        int width = view.getWidth();
        view.setAlpha(0);
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationX", width, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(50);
        set.setDuration(400);
        set.start();
        return set;
    }
    public static Animator slideToRight(View view){
        int width = view.getWidth();
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationX", 0, width);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(50);
        set.setDuration(400);
        set.start();
        return set;
    }
    public static Animator slideToLeft(View view){

        int width = view.getWidth();
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        Animator translationAnimator =  ObjectAnimator.ofFloat(view, "translationX", 0, -width);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator,translationAnimator);
        set.setStartDelay(50);
        set.setDuration(400);
        set.start();
        return set;
    }
    public static Animator moveTo(View view,float deltaX){
        return null;
    }
}
