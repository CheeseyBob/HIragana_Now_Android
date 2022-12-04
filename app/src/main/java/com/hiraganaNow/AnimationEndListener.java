package com.hiraganaNow;

import android.view.animation.Animation;

public class AnimationEndListener implements Animation.AnimationListener {

    private final Runnable onAnimationEnd;

    public AnimationEndListener(Runnable onAnimationEnd) {
        this.onAnimationEnd = onAnimationEnd;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // Not used.
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        onAnimationEnd.run();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // Not used.
    }
}
