package cn.ieclipse.af.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * 
 * timeButton
 * 
 * @author czw
 * @date 2015年12月17日
 *       
 */
public class TimeButton extends Button implements OnClickListener {
    private long totalTime = 60 * 1000;// 默认60秒
    private static long resetTime;
    private String textafter = "秒后重发";
    private String initText;
    private OnClickListener mOnclickListener;
    private Timer timer;
    private TimerTask timeTask;
    private long time;
    Map<String, Long> map = new HashMap<String, Long>();
    
    public TimeButton(Context context) {
        super(context);
        setOnClickListener(this);
        initText = getText().toString();
        
    }
    
    public TimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        initText = getText().toString();
    }
    
    public void start() {
        initTimer();
        this.setText(time / 1000 + textafter);
        this.setEnabled(false);
        timer.schedule(timeTask, 0, 1000);
    }
    
    private void reset() {
        if (initText != null) {
            setText(initText);
        }
    }
    
    @SuppressLint("HandlerLeak")
    Handler han = new Handler() {
        public void handleMessage(android.os.Message msg) {
            TimeButton.this.setText(time / 1000 + textafter);
            time -= 1000;
            if (time < 0) {
                TimeButton.this.setEnabled(true);
                reset();
                clearTimer();
            }
        };
    };
    
    private void initTimer() {
        time = totalTime;
        timer = new Timer();
        timeTask = new TimerTask() {
            
            @Override
            public void run() {
                han.sendEmptyMessage(0x01);
            }
        };
    }
    
    public void clearTimer() {
        if (timeTask != null) {
            timeTask.cancel();
            timeTask = null;
        }
        if (timer != null)
            timer.cancel();
        timer = null;
    }
    
    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof TimeButton) {
            super.setOnClickListener(l);
        }
        else
            this.mOnclickListener = l;
    }
    
    @Override
    public void onClick(View v) {
        if (mOnclickListener != null)
            mOnclickListener.onClick(v);
            
    }
    
    /** * 设置计时时候显示的文本 */
    public TimeButton setTextAfter(String text1) {
        this.textafter = text1;
        return this;
    }
    
    /** * 设置点击之前的文本 */
    public TimeButton setInitText(String text0) {
        this.initText = text0;
        this.setText(initText);
        return this;
    }
    
    /**
     * 设置到计时长度
     * 
     * @param lenght
     *            时间 默认毫秒
     * @return
     */
    public TimeButton setLenght(long lenght) {
        this.totalTime = lenght;
        return this;
    }
    /*
    
    *
    */
}