package com.hiraganaNow;

import android.view.View;

public class SimpleOnLayoutChangeListener implements View.OnLayoutChangeListener {

    private final Runnable runnable;

    public SimpleOnLayoutChangeListener(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        runnable.run();
    }
}
