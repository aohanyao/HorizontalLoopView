package com.aohanyao.loop.widget.adapter;

import android.view.View;

public abstract class LoopViewAdapter<V extends View> {
    private int centerIndex;

    public int getCenterIndex() {
        return centerIndex;
    }

    public void setCenterIndex(int centerIndex) {
        this.centerIndex = centerIndex;
    }


    /**
     * 正在滚动
     *
     * @param scrollView
     * @param position
     */
    public abstract void onScroller(V scrollView, int position);
}