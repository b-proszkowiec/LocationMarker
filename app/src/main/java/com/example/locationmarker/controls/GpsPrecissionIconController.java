package com.example.locationmarker.controls;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.locationmarker.R;

public class GpsPrecissionIconController {

    private static final String LOG_TAG = GpsPrecissionIconController.class.getSimpleName();

    private final int interval = 10000;
    private final int EVENT = 104;
    private boolean isTimesUp;
    private static Button precisionButton;
    private Context context;

    public GpsPrecissionIconController(Context context, Button button) {
        this.context = context;
        this.precisionButton = button;
        isTimesUp = true;
    }

    public void update(String text) {
        precisionButton.setText(text);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_scale);
        precisionButton.startAnimation(animation);
        Message msg = handler.obtainMessage(EVENT);
        handler.sendMessageDelayed(msg, interval);
        isTimesUp = false;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT:
                    if (isTimesUp == true) {
                        precisionButton.setText("N/A");
                    }

                    if (!handler.hasMessages(EVENT)) {
                        handler.sendMessageDelayed(handler.obtainMessage(EVENT), interval);
                        isTimesUp = true;
                    }
                    break;

                default:
                    Log.e(LOG_TAG, "Unrecognized message handled!");
                    break;
            }
        }
    };
}
