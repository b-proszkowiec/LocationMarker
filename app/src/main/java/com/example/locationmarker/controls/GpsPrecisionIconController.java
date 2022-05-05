package com.example.locationmarker.controls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.example.locationmarker.R;
import com.example.locationmarker.fragments.SettingsFragment;
import com.example.locationmarker.settings.OptionSettings;

import static com.example.locationmarker.constants.LocationMarkerConstants.GpsPrecisionIconControllerConstants.NO_LOCATION_UPDATE_TIMEOUT;

public class GpsPrecisionIconController implements IPrecisionIconVisible {

    private static final String LOG_TAG = GpsPrecisionIconController.class.getSimpleName();
    private final float SCALE_FACTOR = 0.7f;

    private final int EVENT = 104;
    private boolean isTimesUp;
    private float startScale = 1;
    private Button precisionButton;
    private View precisionLayout;

    /**
     * Constructor of GpsPrecisionIconController.
     *
     * @param activity Activity to working on.
     */
    public GpsPrecisionIconController(Activity activity) {
        this.precisionButton = activity.findViewById(R.id.precisionButton);
        this.precisionLayout = activity.findViewById(R.id.precisionLayout);
        this.isTimesUp = true;
        SettingsFragment.registerListener(this);
        setPrecisionLayoutVisible(OptionSettings.getInstance().getShowPrecisionIconStatus());
    }

    private float getScaleValue(float accuracy) {
        if (accuracy > 10f) {
            return 1f;
        }
        if (accuracy < 2f) {
            return SCALE_FACTOR;
        }
        float proportion = (accuracy - 2f) / 8f;
        return proportion * (1f - SCALE_FACTOR) + SCALE_FACTOR;
    }


    /**
     * Update value in precision button to the specified.
     *
     * @param accuracy value to show inside precision button.
     */
    public void update(float accuracy) {

        @SuppressLint("DefaultLocale") String text = String.format("%.02f m", accuracy);

        precisionButton.setText(text);
        float endScale = getScaleValue(accuracy);

        Animation animation = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(1000);
        precisionButton.startAnimation(animation);
        startScale = endScale;

        Message msg = handler.obtainMessage(EVENT);
        handler.sendMessageDelayed(msg, NO_LOCATION_UPDATE_TIMEOUT);
        isTimesUp = false;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT:
                    if (isTimesUp) {
                        precisionButton.setText("N/A");
                    }

                    if (!handler.hasMessages(EVENT)) {
                        handler.sendMessageDelayed(handler.obtainMessage(EVENT), NO_LOCATION_UPDATE_TIMEOUT);
                        isTimesUp = true;
                    }
                    break;

                default:
                    Log.e(LOG_TAG, "Unrecognized message handled!");
                    break;
            }
        }
    };

    private void setPrecisionLayoutVisible(boolean visibility) {
        if (visibility) {
            precisionLayout.setVisibility(View.VISIBLE);
        } else {
            precisionLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPrecisionIconVisibleChange(boolean visibility) {
        setPrecisionLayoutVisible(visibility);
    }
}
