package com.bpr.pecka.controls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.bpr.pecka.R;
import com.bpr.pecka.fragments.SettingsFragment;
import com.bpr.pecka.settings.OptionSettings;

import static com.bpr.pecka.constants.LocationMarkerConstants.GpsPrecisionIconControllerConstants.NO_LOCATION_UPDATE_TIMEOUT;

import java.util.Locale;

public class GpsPrecisionIconController implements IPrecisionIconVisible {

    private static final String LOG_TAG = GpsPrecisionIconController.class.getSimpleName();
    private static final float SCALE_FACTOR = 0.7f;
    private static final int EVENT = 104;
    private final Button precisionButton;
    private final View precisionLayout;
    private boolean isTimesUp;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == EVENT) {
                if (isTimesUp) {
                    precisionButton.setText("N/A");
                }
                if (!hasMessages(EVENT)) {
                    sendMessageDelayed(obtainMessage(EVENT), NO_LOCATION_UPDATE_TIMEOUT);
                    isTimesUp = true;
                }
            } else {
                Log.e(LOG_TAG, "Unrecognized message handled!");
            }
        }
    };
    private float startScale = 1;

    /**
     * Constructor of GpsPrecisionIconController.
     *
     * @param activity Activity to work on.
     */
    public GpsPrecisionIconController(Activity activity) {
        this.precisionButton = activity.findViewById(R.id.precisionButton);
        this.precisionLayout = activity.findViewById(R.id.precisionLayout);
        this.isTimesUp = true;
        SettingsFragment.registerListener(this);
        setPrecisionLayoutVisible(OptionSettings.getInstance().isShowPrecisionIconStatus());
    }

    private float getScaleValue(float accuracy) {
        if (accuracy > 10f) return 1f;
        if (accuracy < 2f) return SCALE_FACTOR;
        float proportion = (accuracy - 2f) / 8f;
        return proportion * (1f - SCALE_FACTOR) + SCALE_FACTOR;
    }

    /**
     * Update value in precision button to the specified.
     *
     * @param accuracy value to show inside precision button.
     */
    public void update(float accuracy) {
        precisionButton.setText(String.format(Locale.getDefault(),"%.02f m", accuracy));

        float endScale = getScaleValue(accuracy);
        Animation animation = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setFillAfter(true);
        animation.setDuration(1000);
        precisionButton.startAnimation(animation);
        startScale = endScale;

        handler.removeMessages(EVENT);
        handler.sendEmptyMessageDelayed(EVENT, NO_LOCATION_UPDATE_TIMEOUT);
        isTimesUp = false;
    }

    private void setPrecisionLayoutVisible(boolean visibility) {
        precisionLayout.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onPrecisionIconVisibleChange(boolean visibility) {
        setPrecisionLayoutVisible(visibility);
    }
}
