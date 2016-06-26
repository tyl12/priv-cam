package org.yanzi.activity;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

/**
 * 实现监听左右滑动的事件，哪个view需要的时候直接setOnTouchListener就可以用了
 * @author LinZhiquan
 *
 */
public class GestureListener extends SimpleOnGestureListener implements OnTouchListener {
    /**
     * 左右滑动的最短距离
     */
    private int distance = 100;
    /**
     * 左右滑动的最大速度
     */
    private int velocity = 200;

    private GestureDetector gestureDetector;

    private Context mContext;

    public GestureListener(Context context) {
        super();
        mContext = context;
        gestureDetector = new GestureDetector(context, this);
    }

    /**
     * 向左滑的时候调用的方法，子类应该重写
     *
     * @return
     */
    public boolean left() {
        return false;
    }

    /**
     * 向右滑的时候调用的方法，子类应该重写
     *
     * @return
     */
    public boolean right() {
        return false;
    }

    public boolean up() {
        return false;
    }

    public boolean down() {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        // e1：第1个ACTION_DOWN MotionEvent
        // e2：最后一个ACTION_MOVE MotionEvent
        // velocityX：X轴上的移动速度（像素/秒）
        // velocityY：Y轴上的移动速度（像素/秒）

        // 向左滑
        if (e1.getX() - e2.getX() > distance
                && Math.abs(velocityX) > velocity) {
            left();
        }
        // 向右滑
        if (e2.getX() - e1.getX() > distance
                && Math.abs(velocityX) > velocity) {
            right();
        }

        // 向上滑
        if (e1.getY() - e2.getY() > distance
                && Math.abs(velocityX) > velocity) {
            up();
        }
        // 向下滑
        if (e2.getY() - e1.getY() > distance
                && Math.abs(velocityX) > velocity) {
            down();
        }

        return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        onTouch(v, event);
        gestureDetector.onTouchEvent(event);
        return false;
    }


    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    //----------------------
    // 连续 touch 的最大间隔, 超过该间隔将视为一次新的 touch 开始
    private long multiTouchInterval = 300;
    // 上次 onTouch 发生的时间
    private long lastTouchTime = 0;
    // 已经连续 touch 的次数
    private int touchCount = 0;

    /*
    public GestureListener() {
        this(250);
    }

    public GestureListener(long multiTouchInterval) {
        this.multiTouchInterval = multiTouchInterval;
        this.lastTouchTime = 0;
        this.touchCount = 0;
    }
    */

    /*
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("##@@##", "onTouch: ACTION_DOWN");
            final long now = System.currentTimeMillis();
            this.lastTouchTime = now;

            synchronized (this) {
                this.touchCount++;
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 两个变量相等, 表示时隔 multiTouchInterval 之后没有新的 touch 产生, 触发事件并重置 touchCount
                    if (now == GestureListener.this.lastTouchTime) {
                        synchronized (GestureListener.this) {
                            GestureListener.this.onMultiTouch(v, event, GestureListener.this.touchCount);
                            GestureListener.this.touchCount = 0;
                        }
                    }
                }
            }, GestureListener.this.multiTouchInterval);
        }
        super.onTouch(v,event);
        return true;
    }

    public boolean onMultiTouch(View v, MotionEvent event, int touchCount){
        return true;
    }
    */
}