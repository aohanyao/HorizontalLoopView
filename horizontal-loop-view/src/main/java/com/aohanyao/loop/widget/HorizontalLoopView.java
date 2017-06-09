package com.aohanyao.loop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.aohanyao.loop.widget.adapter.LoopViewAdapter;
import com.aohanyao.loop.widget.util.DensityUtils;

/**
 * Created by aohanyao on 2017/6/5 0005.
 * <p>version:1.0.0</p>
 * <p>* desHorizontal Loop View</p>
 * <p>github:https://github.com/aohanyao</p>
 */
public class HorizontalLoopView extends LinearLayout {
    private final String TAG = "HorizontalLoopView";
    private boolean isLoop = true;
    private int mChildWidth = 70;
    /**
     * all child sum width
     */
    private int mChildrenSumWidth;
    /**
     * scroll offset
     */
    private int mInitialOffset;
    /**
     * X offset
     */
    private int mScrollX;
    /**
     * last scroll
     */
    private int mLastScroll;
    private Scroller mScroller;
    /**
     * min scroll speed
     */
    private int mMinimumVelocity;
    /**
     * max scroll speed
     */
    private int mMaximumVelocity;
    /**
     * 无
     */
    private final int NO_MODE = 0;
    /**
     * 拖动模式
     */
    private final int DRAG_MODE = 1;
    /**
     * 点击模式
     */
    private final int CLICK_MODE = 2;
    /**
     * 滑动模式
     */
    private final int FLING_MODE = 3;
    /**
     * 以滚动到中心
     */
    private final int MOVE_CENTER_MODE = 4;
    /**
     * 下标的位置key
     */
    private final int INDEX_TAG = R.id.horizontal_loop_view_key;
    private int stateMode = NO_MODE;
    private int mLastX;
    private int mFirstX;
    private VelocityTracker mVelocityTracker;
    private int mDataIndex;
    private View mCenterView;
    private LoopViewAdapter loopViewAdapter;

    public HorizontalLoopView(Context context) {
        this(context, null);
    }

    public HorizontalLoopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalLoopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
        initScroll();
        mDataIndex = 0;
    }

    /**
     * 设置适配器
     *
     * @param loopViewAdapter
     */
    public void setLoopViewAdapter(LoopViewAdapter loopViewAdapter) {
        if (loopViewAdapter == null) {
            throw new RuntimeException("loopViewAdapter is null");
        }
        this.loopViewAdapter = loopViewAdapter;
        this.mChildWidth = loopViewAdapter.getChildWidth();
        initView();
    }

    /**
     * 初始化一些滚动的属性
     */
    private void initScroll() {
        mScroller = new Scroller(getContext());
        //水平垂直
        setGravity(Gravity.CENTER_VERTICAL);
        //水平方向
        setOrientation(HORIZONTAL);
        //视图滚动配置
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        //获取最小滚动距离
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        //最大滚动距离
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 开始初始化数据
     */
    private void initView() {
        //获取屏幕的宽度
        final int displayWidth = DensityUtils.getWindowMsg(getContext())[0];
        //计算View的个数
        int childCount = displayWidth / mChildWidth;

        //确保子view的数量为奇数
        if (childCount % 2 == 0) {
            childCount++;
        }
        //左右各增加一个
        childCount += 2;
        //先清除所有的View
        removeAllViews();
        //获取居中的子view下标
        int mCenterIndex = childCount / 2;

        mDataIndex = loopViewAdapter.getCenterIndex();
        //添加子view
        for (int i = 0; i < childCount; i++) {
            View childView = loopViewAdapter.getView(i, i == mCenterIndex);
            childView.setSelected(i == mCenterIndex);
            //添加
            addView(childView);
        }

        //为View设置数据start
        //中间
        mCenterView = getChildAt(mCenterIndex);
        //设置数据
        loopViewAdapter.setData(mCenterView, mDataIndex);
        //设置下标tag
        mCenterView.setTag(INDEX_TAG, mDataIndex);
        //回调默认选中
        loopViewAdapter.onSelect(mCenterView, mDataIndex);

        //右边  中间 5 6 7
        for (int i = mCenterIndex + 1, j = mDataIndex + 1; i < childCount; i++, j++) {
            if (j == loopViewAdapter.getItemCount()) {
                j = 0;
            }
            View childAtView = getChildAt(i);
            loopViewAdapter.setData(childAtView, j);
            childAtView.setTag(INDEX_TAG, j);
        }

        //左边
        for (int i = mCenterIndex - 1, j = mDataIndex - 1; i >= 0; i--, j--) {
            if (j < 0) {
                //回到最后一个
                j = loopViewAdapter.getItemCount() - 1;
            }
            View childAtView = getChildAt(i);
            loopViewAdapter.setData(childAtView, j);
            childAtView.setTag(INDEX_TAG, j);
        }
        //为TextView设置数据end


        //最后计算整个宽度
        mChildrenSumWidth = mChildWidth * childCount;
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {//滚动未完成
            //获得当前的X轴位置
            mScrollX = mScroller.getCurrX();
            //滚动到相应的坐标
            reScrollTo(mScrollX, 0);
            //重绘
            postInvalidate();
        } else {//滚动完成
            if (stateMode == FLING_MODE) {
                stateMode = MOVE_CENTER_MODE;
                selectPosition(getChildCount() / 2);
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        reScrollTo(x, y);
        selectPosition(getChildCount() / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //计算 居中view的偏移量 所有子view的宽度与宽度之差的一半
        mInitialOffset = (mChildrenSumWidth - w) / 2;
        //滚动到中间
        super.scrollTo(mInitialOffset, 0);
        mScrollX = mInitialOffset;
        mLastScroll = mInitialOffset;
    }

    protected void reScrollTo(int x, int y) {
        //获取X轴滚动
        int scrollX = getScrollX();
        //获得滚动差
        int scrollDiff = x - mLastScroll;
        //遍历所有的子view
        if (getChildCount() > 0) {
            scrollX += scrollDiff;

            //向右滑动  负数
            if (scrollX - mInitialOffset > mChildWidth / 2) {
                //获得相对滚动的距离
                int relativeScroll = scrollX - mInitialOffset;
                //计算向右滚动了多少个
                int stepsRight = (relativeScroll + (mChildWidth / 2)) / mChildWidth;
                //移动view
                moveChildres(-stepsRight);
                //计算的X轴滚动值
                scrollX = ((relativeScroll - mChildWidth / 2) % mChildWidth) + mInitialOffset - mChildWidth / 2;
            } else if (mInitialOffset - scrollX > mChildWidth / 2) {//向前滑动
                //获得相对滚动的距离
                int relativeScroll = mInitialOffset - scrollX;
                //计算向左滚动了多少个
                int stepsLeft = (relativeScroll + (mChildWidth / 2)) / mChildWidth;
                //移动view
                moveChildres(stepsLeft);
                //计算的X轴滚动值
                scrollX = (mInitialOffset + mChildWidth / 2 - ((mInitialOffset + mChildWidth / 2 - scrollX) % mChildWidth));
            }
        }
        super.scrollTo(scrollX, y);

        //判断 回调 选中事件
        if (mCenterView != null && mScroller.isFinished()) {
            if (stateMode == CLICK_MODE || stateMode == MOVE_CENTER_MODE) {
                if (loopViewAdapter != null) {
                    loopViewAdapter.onSelect(mCenterView, ((int) mCenterView.getTag(INDEX_TAG)));
                }
            }
        }
        mLastScroll = x;
    }

    /**
     * 移动 元素的值
     *
     * @param steps
     */
    protected void moveChildres(int steps) {

        if (steps == 0 || loopViewAdapter == null) {
            return;
        }
        int start;
        int end;
        int incr;
        if (steps < 0) {
            start = 0;
            end = getChildCount();
            incr = 1;
        } else {
            start = getChildCount() - 1;
            end = -1;
            incr = -1;
        }
        for (int i = start; i != end; i += incr) {
            //获取View
            View childAtView = getChildAt(i);
            //获取tag中的数据
            int mNowIndex = (int) childAtView.getTag(INDEX_TAG);
            //向右滑动
            if (steps > 0) {
                //当前为0了，赋值为最后一个
                if (mNowIndex == 0) {
                    mNowIndex = loopViewAdapter.getItemCount();
                }
                mNowIndex--;
            } else {
                //向左滑动 已经是最后一个 赋值为第一个
                if (mNowIndex == loopViewAdapter.getItemCount() - 1) {
                    mNowIndex = -1;
                }
                mNowIndex++;
            }

            //保存选中的View
            if (childAtView.isSelected()) {
                mCenterView = childAtView;
            }
            //保存当前的下标
            childAtView.setTag(INDEX_TAG, mNowIndex);

            //适配器不为空  回调数据
            if (loopViewAdapter != null) {
                loopViewAdapter.setData(childAtView, mNowIndex);
            }

        }
    }


    /**
     * finding whether to scroll or not
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        //按下
        if (action == MotionEvent.ACTION_DOWN) {
            stateMode = CLICK_MODE;
            //动画未完成 直接结束
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
        }

        if (stateMode == NO_MODE)
            return super.onTouchEvent(ev);

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mFirstX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                //偏移量大于10 代表是在滑动找那个
                if (Math.abs(mFirstX - x) > 10) {
                    stateMode = DRAG_MODE;
                }
                //计算滚动位置
                mScrollX += mLastX - x;
                //滚动到相应的位置
                reScrollTo(mScrollX, 0);
                break;
            case MotionEvent.ACTION_UP:
                //获得速度追踪器
                final VelocityTracker velocityTracker = mVelocityTracker;
                //调用滑动 300ms
                velocityTracker.computeCurrentVelocity(300);
                //获得初始速度
                int initialVelocity = (int) Math.min(velocityTracker.getXVelocity(), mMaximumVelocity);
                //当前的初始速度大于最小速度->滚动
                if (getChildCount() > 0 && Math.abs(initialVelocity) > mMinimumVelocity) {
                    //滚动
                    fling(-initialVelocity);
                    //赋值当前模式为滑动模式
                    stateMode = FLING_MODE;
                } else {
                    //点击
                    if (stateMode == CLICK_MODE) {
                        //处理点击
                        clickFunction(ev);
                    } else {
                        //移动一格
                        stateMode = MOVE_CENTER_MODE;
                        //选中位置
                        selectPosition(getChildCount() / 2);
                    }
                }
            case MotionEvent.ACTION_CANCEL:
            default:
        }
        mLastX = x;

        return true;
    }

    /**
     * 事件点击
     *
     * @param ev
     */
    public void clickFunction(MotionEvent ev) {
        int x = (int) ev.getX();
        int selectIndex = (x + getScrollX()) / mChildWidth;
        selectPosition(selectIndex);
    }


    /**
     * 选中
     *
     * @param selectIndex 中间的位置
     */
    public void selectPosition(int selectIndex) {
        //当前子view的个数
        int childCount = getChildCount();
        //中间的位置
        final int centerIndex = (childCount / 2);
        //中间的X轴坐标
        int centerX = getWidth() / 2;
        //获取当前滑动的X坐标   每个子View的宽度*当前选择的下标 - 滚动的X + 半个View的宽度
        int posX = mChildWidth * selectIndex - getScrollX() + mChildWidth / 2;
        //获取差别
        int diff = posX - centerX;
        //计算移动了几个
        int count = Math.abs(selectIndex - centerIndex);
        count = (count == 0) ? 1 : count;
        //滚动到相应的位置
        mScroller.startScroll(mScrollX, 0, diff, 0, 800 * count);
        //重绘
        postInvalidate();
    }


    /**
     * 开始滑动，飞速滑动
     *
     * @param velocityX
     */
    private void fling(int velocityX) {
        if (getChildCount() > 0) {
            mScroller.fling(mScrollX, 0, velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            invalidate();
        }
    }
}
