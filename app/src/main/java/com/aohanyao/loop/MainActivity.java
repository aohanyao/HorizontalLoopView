package com.aohanyao.loop;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aohanyao.loop.widget.HorizontalLoopView;
import com.aohanyao.loop.widget.adapter.LoopViewAdapter;
import com.aohanyao.loop.widget.util.DensityUtils;
import com.bumptech.glide.Glide;

public class MainActivity extends Activity {
    private Activity mActivity;
    private HorizontalLoopView hlv1;
    private TextView tv1;
    private String[] mImages;
    private ImageView iv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        initHlv1();

        initH1v2();
    }

    /**
     * 初始化视图二
     */
    private void initH1v2() {
        HorizontalLoopView hlv2 = (HorizontalLoopView) findViewById(R.id.hlv2);
        iv2 = (ImageView) findViewById(R.id.iv2);


        mImages = getResources().getStringArray(R.array.images_url);
        hlv2.setLoopViewAdapter(new LoopViewAdapter<View>() {
            @Override
            protected int setCenterIndex() {
                return mImages.length / 2;
            }

            @Override
            public int getChildWidth() {
                return DensityUtils.dp2px(mActivity, 80f);
            }

            @Override
            public int getItemCount() {
                return mImages.length;
            }

            @Override
            public View getView(int position, boolean isCenter) {
//                ImageView view = new ImageView(mActivity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                View view = getLayoutInflater().inflate(R.layout.layout_image, null);
                view.setSelected(isCenter);
                params.width = getChildWidth();
                view.setLayoutParams(params);

                if (isCenter) {
                    ImageView iv = (ImageView) view.findViewById(R.id.iv);
                    int margin = DensityUtils.dp2px(mActivity, 2);
                    ViewGroup.MarginLayoutParams marginLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    marginLayoutParams.leftMargin = margin;
                    marginLayoutParams.rightMargin = margin;
                    marginLayoutParams.topMargin = margin;
                    marginLayoutParams.bottomMargin = margin;
                    iv.setLayoutParams(marginLayoutParams);
                }


                return view;
            }

            @Override
            public void setData(View scrollView, int position) {
                ImageView iv = (ImageView) scrollView.findViewById(R.id.iv);

                Glide.with(mActivity)
                        .load(mImages[position])
                        .into(iv);
            }

            @Override
            public void onSelect(View selectView, int position) {
                Glide.with(mActivity)
                        .load(mImages[position])
                        .into(iv2);
            }
        });
    }

    private void initHlv1() {
        hlv1 = (HorizontalLoopView) findViewById(R.id.hlv1);
        tv1 = (TextView) findViewById(R.id.tv1);

        final String[] mMonths = getResources().getStringArray(R.array.months_chines);

        //LoopViewAdapter支持泛型，只要是view的子类即可
        hlv1.setLoopViewAdapter(new LoopViewAdapter() {
            @Override
            protected int setCenterIndex() {
                //你要默认居中的下标
                return 0;
            }

            @Override
            public int getChildWidth() {
                //每个view的宽度 单位是px
                return 0;
            }

            @Override
            public int getItemCount() {
                //数量
                return 0;
            }

            @Override
            public View getView(int position, boolean isCenter) {
                //根据下标返回相应的view ,  isCenter 为true，返回中间的view，可以做一些其他的样式
                return null;
            }

            @Override
            public void setData(View scrollView, int position) {
                //根据下标 对对应的View 做相应的数据处理
            }

            @Override
            public void onSelect(View selectView, int position) {
                //当数据选中的时候回调
            }
        });


        hlv1.setLoopViewAdapter(new LoopViewAdapter<TextView>() {
            @Override
            protected int setCenterIndex() {
                return 5;
            }

            @Override
            public int getChildWidth() {
                return DensityUtils.dp2px(mActivity, 80F);
            }

            @Override
            public int getItemCount() {
                return mMonths.length;
            }

            @Override
            public TextView getView(int position, boolean isCenter) {
                //创建TextView
                TextView textView = new TextView(mActivity);
                //布局参数
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.width = getChildWidth();
                textView.setLayoutParams(params);

                //test 选中中心的那个
                textView.setSelected(isCenter);
                textView.setBackgroundResource(R.drawable.select_text_bg);
                textView.setTextColor(getResources().getColorStateList(R.color.select_text_text_color));
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(DensityUtils.sp2px(mActivity, 12));
                return textView;
            }

            @Override
            public void setData(TextView scrollView, int position) {
                //设置数据
                scrollView.setText(mMonths[position]);
            }

            @Override
            public void onSelect(TextView selectView, int position) {
                tv1.setText("已选择:" + position);
            }
        });
    }
}
