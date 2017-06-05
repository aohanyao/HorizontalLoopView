package com.aohanyao.loop;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.aohanyao.loop.widget.HorizontalLoopView;
import com.aohanyao.loop.widget.adapter.LoopViewAdapter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHlv1();
    }

    private void initHlv1() {
        HorizontalLoopView hlv1 = (HorizontalLoopView) findViewById(R.id.hlv1);
        final String[] mMonths = getResources().getStringArray(R.array.months_chines);

        hlv1.setLoopViewAdapter(new LoopViewAdapter<TextView>() {
            @Override
            public void onScroller(TextView scrollView, int position) {
                //设置数据
                scrollView.setText(mMonths[position]);
            }
        });
    }
}
