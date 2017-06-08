# HorizontalLoopView

水平的无限滑动的选择View，绝不会OOM哦。先看截图

  ![](ScreensHots/screens_hots.gif)

###版本

[![](https://jitpack.io/v/helen-x/JitpackReleaseDemo.svg)](https://jitpack.io/#aohanyao/HorizontalLoopView)


### 使用

像使用RecyclerView那样使用

### setp1

	repositories {
			...
			maven { url 'https://jitpack.io' }
		}


### step2

	dependencies {
	        compile 'com.github.aohanyao:HorizontalLoopView:v1.0.0'
	}


### step3

	 <com.aohanyao.loop.widget.HorizontalLoopView
            android:id="@+id/hlv1"
            android:layout_width="match_parent"
            android:layout_height="80dp"/>


### step4

	 HorizontalLoopView  hlv1 = (HorizontalLoopView) findViewById(R.id.hlv1);
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

具体请直接看demo