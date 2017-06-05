package com.aohanyao.loop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.aohanyao.loop.widget.adapter.LoopViewAdapter;
import com.aohanyao.loop.widget.util.DensityUtils;

/**
 * Created by 江俊超 on 2017/6/5 0005.
 * <p>版本:1.0.0</p>
 * <b>说明<b>水平的循环选择<br/>
 * <li>外接方式，支持任意View</li>
 */
public class HorizontalLoopView extends LinearLayout {
    private int mChildWidth = 70;
    /**
     * 整个View的宽度
     */
    private int mChildrenSumWidth;
    /**
     * 滚动偏移量
     */
    private int mInitialOffset;
    /**
     * X轴滚动的量
     */
    private int mScrollX;
    /**
     * 上一次滚动的量
     */
    private int mLastScroll;
    private Scroller mScroller;
    /**
     * 最小滚动速度
     */
    private int mMinimumVelocity;
    /**
     * 最大滚动速度
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
        ///initAttr(context, attrs);
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
     * 初始化相关属性
     *
     * @param context 上下文
     * @param attrs   属性
     */
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HorizontalLoopView);
        //每个item的宽度
        mChildWidth = (int) array.getDimension(R.styleable.HorizontalLoopView_child_width, DensityUtils.dp2px(context, mChildWidth));
        array.recycle();
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
//        initView();
    }

    /**
     * 开始初始化数据
     */
    private void initView() {
        //获取屏幕分辨率
        final Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //获取屏幕的宽度
        final int displayWidth = display.getWidth();
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
                moveElements(-stepsRight);
                //计算的X轴滚动值
                scrollX = ((relativeScroll - mChildWidth / 2) % mChildWidth) + mInitialOffset - mChildWidth / 2;
            } else if (mInitialOffset - scrollX > mChildWidth / 2) {//向前滑动
                //获得相对滚动的距离
                int relativeScroll = mInitialOffset - scrollX;
                //计算向左滚动了多少个
                int stepsLeft = (relativeScroll + (mChildWidth / 2)) / mChildWidth;
                //移动view
                moveElements(stepsLeft);
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

    protected void moveElements(int steps) {
        if (steps == 0 || loopViewAdapter == null) {
            return;
        }
        int start;
        int end;
        int incr;
        //向右滑动
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
            if (steps > 0) {
                //已经是第0个
                if (mNowIndex == 0) {
                    //重置为item的总个数
                    mNowIndex = loopViewAdapter.getItemCount();
                }
                //自减
                mNowIndex--;
            } else {
                //当前下标已等于总item数
                if (mNowIndex == loopViewAdapter.getItemCount() - 1) {
                    //重置为负一  自加后为0
                    mNowIndex = -1;
                }
                mNowIndex++;
            }

            //获取中间的view
            if (childAtView.isSelected()) {
                mCenterView = childAtView;
            }
            //设置tag
            childAtView.setTag(INDEX_TAG, mNowIndex);

            //回调给前台 设置数据
            if (loopViewAdapter != null) {
                loopViewAdapter.setData(childAtView, mNowIndex);
            }

        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        if (action == MotionEvent.ACTION_DOWN) {
            stateMode = CLICK_MODE;
            //滚动动画未完成
            if (!mScroller.isFinished()) {
                //停止当前滚动的动画
                mScroller.abortAnimation();
            }
        }

        //不在任何模式 直接跳过
        if (stateMode == NO_MODE)
            return super.onTouchEvent(ev);


        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        //转交事件
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
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(300);
                int initialVelocity = (int) Math.min(velocityTracker.getXVelocity(), mMaximumVelocity);

                if (getChildCount() > 0 && Math.abs(initialVelocity) > mMinimumVelocity) {
                    fling(-initialVelocity);
                    stateMode = FLING_MODE;
                } else {
                    if (stateMode == CLICK_MODE) {
                        clickFunction(ev);
                    } else {
                        stateMode = MOVE_CENTER_MODE;
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
        int childCount = getChildCount();
        final int centerIndex = (childCount / 2);
        int centerX = getWidth() / 2;
        int posX = mChildWidth * selectIndex - getScrollX() + mChildWidth / 2;
        int diff = posX - centerX;
        int count = Math.abs(selectIndex - centerIndex);
        count = (count == 0) ? 1 : count;
        mScroller.startScroll(mScrollX, 0, diff, 0, 800 * count);
        postInvalidate();
    }


    /**
     * 恢复滚动
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
