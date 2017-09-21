package com.aohanyao.loop.widget.adapter;

import android.view.View;

/**
 * 水平loopview 的适配器
 *
 * @param <V>
 */
public abstract class LoopViewAdapter<V extends View> {

    /**
     * 获取中心下标，
     *
     * @return
     */
    public int getCenterIndex() {
        return setCenterIndex();
    }

    /**
     * 中心下标
     *
     * @return
     */
    protected abstract int setCenterIndex();

    /**
     * 子view的宽度
     * childWidth 单位 PX 像素
     *
     * @return
     */
    public abstract int getChildWidth();

    /**
     * 获取数量
     *
     * @return
     */
    public abstract int getItemCount();

    /**
     * 获取视图
     *<p>返回数据视图</p>
     * @param position 数据的位置
     * @param isCenter 是否是中间的位置  以返回不同的布局
     * @return
     */
    public abstract V getView(int position, boolean isCenter);

    /**
     * 正在滚动
     * <p>请在这里设置数据</p>
     *
     * @param scrollView 正在滚动的视图
     * @param position   数据的位置
     */
    public abstract void setData(V scrollView, int position);

    /**
     * 视图被选中
     *
     * @param selectView 被选中的视图
     * @param position   数据的位置
     */
    public abstract void onSelect(V selectView, int position);
}