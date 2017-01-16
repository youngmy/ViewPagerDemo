package com.young.viewpagerdemo;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Android 如何实现一个平滑过渡的ViewPager广告条
 */
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private List<ImageView> vpLists;
    private LinearLayout ll_dot_group; //用来添加小圆点
    private String[] imageDescArrs;
    private TextView tv_img_desc;
    private ViewPager vp;

    private boolean isSwitchPager = false; //默认不切换
    private int previousPosition = 0; //默认为0

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //更新当前viewpager的 要显示的当前条目
            vp.setCurrentItem(vp.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        vp = (ViewPager) findViewById(R.id.vp);
        ll_dot_group = (LinearLayout) findViewById(R.id.ll_dot_group);
        tv_img_desc = (TextView) findViewById(R.id.tv_img_desc);

        initViewPagerData();
        vp.setAdapter(new ViewpagerAdapter());

        //设置当前viewpager要显示第几个条目
        int item = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % vpLists.size());
        vp.setCurrentItem(item);

        //把第一个小圆点设置为白色，显示第一个textview内容
        ll_dot_group.getChildAt(previousPosition).setEnabled(true);
        tv_img_desc.setText(imageDescArrs[previousPosition]);
        //设置viewpager滑动的监听事件
        vp.addOnPageChangeListener(this);

        //实现自动切换的功能
        new Thread() {
            public void run() {
                while (!isSwitchPager) {
                    SystemClock.sleep(3000);
                    //拿着我们创建的handler 发消息
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    /**
     * 初始化ViewPager的数据
     */
    private void initViewPagerData() {
        imageDescArrs = new String[]{"标题1", "标题2", "标题3", "标题4", "标题5"};
        vpLists = new ArrayList<ImageView>();
        int imgIds[] = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        ImageView iv;
        View dotView;

        for (int i = 0; i < imgIds.length; i++) {
            iv = new ImageView(this);
            iv.setBackgroundResource(imgIds[i]);
            vpLists.add(iv);
            //准备小圆点的数据
            dotView = new View(getApplicationContext());
            dotView.setBackgroundResource(R.drawable.selector_dot);
            //设置小圆点的宽和高
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
            //设置每个小圆点之间距离
            if (i != 0) {
                params.leftMargin = 15;
            }
            dotView.setLayoutParams(params);
            //设置小圆点默认状态
            dotView.setEnabled(false);
            //把dotview加入到线性布局中
            ll_dot_group.addView(dotView);
        }
    }

    /**
     * 定义数据适配器
     */
    private class ViewpagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //是否复用当前view对象
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        //初始化每个条目要显示的内容
        @Override
        public Object instantiateItem(ViewGroup container,  int position) {
            //拿着position位置 % 集合.size
            final int newposition = position % vpLists.size();
            //获取到条目要显示的内容imageview
            ImageView iv = vpLists.get(newposition);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this,"点击了第"+newposition+"张",Toast.LENGTH_SHORT).show();
                }
            });
            //要把 iv加入到 container 中
            container.addView(iv);
            return iv;
        }

        //销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //移除条目
            container.removeView((View) object);
        }
    }

    //当页面开始滑动
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //当新的页面被选中的时候调用
    @Override
    public void onPageSelected(int position) {
        //拿着position位置 % 集合.size
        int newposition = position % vpLists.size();
        //取出postion位置的小圆点 设置为true
        ll_dot_group.getChildAt(newposition).setEnabled(true);
        //把一个小圆点设置为false
        ll_dot_group.getChildAt(previousPosition).setEnabled(false);
        tv_img_desc.setText(imageDescArrs[newposition]);
        previousPosition = newposition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        //当Activity销毁的时候 把是否切换的标记置为true
        isSwitchPager = true;
        super.onDestroy();
    }

}
