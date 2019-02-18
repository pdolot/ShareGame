package com.example.patryk.sharegame2.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.patryk.sharegame2.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.os.SystemClock.sleep;

public class LoadingDialog extends Dialog{

    private RelativeLayout layout;
    private Thread drawCircle, drawRect;
    private Timer timer;
    private TimerTask task;
    private ProgressBar topCircle;
    private ProgressBar bottomCircle;
    private ProgressBar leftBar;
    private ProgressBar rightBar;
    private boolean progress = true;
    private Context context;


    public LoadingDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_dialog);

        topCircle = findViewById(R.id.topCircle);
        bottomCircle = findViewById(R.id.bottomCircle);
        leftBar = findViewById(R.id.leftBar);
        rightBar = findViewById(R.id.rightBar);
        layout = findViewById(R.id.layoutIntro);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        task = new TimerTask() {
            @Override
            public void run() {

                drawCircle = new Thread() {
                    @Override
                    public void run() {
                        if (progress == true) {
                            for (int i = 0; i <= 50; i++) {
                                try {
                                    sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                topCircle.setProgress(i);
                                bottomCircle.setProgress(i);
                            }
                        } else {
                            for (int i = 50; i >= 0; i--) {
                                try {
                                    sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                topCircle.setProgress(i);
                                bottomCircle.setProgress(i);
                            }
                        }
                    }
                };

                drawRect = new Thread() {
                    @Override
                    public void run() {
                        if (progress == true) {
                            for (int i = 0; i <= 50; i++) {
                                try {
                                    sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                leftBar.setProgress(i);
                                rightBar.setProgress(i);
                            }
                        } else {
                            for (int i = 50; i >= 0; i--) {
                                try {
                                    sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                leftBar.setProgress(i);
                                rightBar.setProgress(i);
                            }
                        }
                    }
                };

                if(progress){
                    drawCircle.start();
                    sleep(500);
                    drawRect.start();
                    sleep(1000);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setAnimation(0,180);
                        }
                    });
                }else{
                    drawRect.start();
                    sleep(500);
                    drawCircle.start();
                    sleep(1000);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setAnimation(180,0);
                        }
                    });
                }

                sleep(500);

                if (progress) {
                    progress = false;
                } else {
                    progress = true;
                }

            }
        };
        timer = new Timer();
        timer.schedule(task, 01, 1000l);
    }


    public void setAnimation(int from, int to) {
        RotateAnimation animation = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());
        layout.setAnimation(animation);
        layout.startAnimation(animation);
    }

}
