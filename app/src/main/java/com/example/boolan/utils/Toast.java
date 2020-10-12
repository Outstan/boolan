package com.example.boolan.utils;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.boolan.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义Toast - 正确提示
 * Created by xiaoshuai on 2018/10/10.
 */

public class Toast {
    private boolean canceled = true;
    private Handler handler;
    private android.widget.Toast toast;
    private TextView toast_content;
    private ImageView toast_img;

    private static Toast instance;

    public static Toast getInstance(Context context) {
        if(instance == null) {
            instance = new Toast(context);
        }
        return instance;
    }

    public Toast(Context context) {
        this(context, new Handler());
    }

    public Toast(Context context, Handler handler) {
        this.handler = handler;

        View layout = LayoutInflater.from(context).inflate(R.layout.toast, null, false);
        toast_content = layout.findViewById(R.id.tv_toast);
        toast_img = layout.findViewById(R.id.iv_toast);
        if (toast == null) {
            toast = new android.widget.Toast(context);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(android.widget.Toast.LENGTH_LONG);
        toast.setView(layout);
    }

    /**
     * @param text     要显示的内容
     * @param img      显示的提示图片
     */
    public void show(String text,int img) {
        toast_content.setText(text);
        toast_img.setImageDrawable(toast_img.getResources().getDrawable(img));
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, 2000 );
    }

    public void show1(String text, int img) {
        toast_content.setText(text);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, 800);
    }
}
