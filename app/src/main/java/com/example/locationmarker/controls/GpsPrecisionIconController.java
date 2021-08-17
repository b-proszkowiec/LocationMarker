package com.example.locationmarker.controls;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.preference.SwitchPreferenceCompat;

import com.example.locationmarker.R;
import com.example.locationmarker.fragments.SettingsFragment;
import com.example.locationmarker.settings.OptionSettings;

import static com.example.locationmarker.constants.LocationMarkerConstants.GpsPrecisionIconControllerConstants.NO_LOCATION_UPDATE_TIMEOUT;

public class GpsPrecisionIconController implements IPrecisionIconVisible {

    private static final String LOG_TAG = GpsPrecisionIconController.class.getSimpleName();

    private final int EVENT = 104;
    private boolean isTimesUp;
    private Button precisionButton;
    private View precisionLayout;
    private Activity activity;
    private Context context;

    public GpsPrecisionIconController(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.precisionButton = activity.findViewById(R.id.precisionButton);
        this.precisionLayout = activity.findViewById(R.id.precisionLayout);
        this.isTimesUp = true;
        SettingsFragment.registerListener(this);


        setPrecisionLayoutVisible(OptionSettings.getInstance().getShowPrecisionIconStatus());
    }

    public void update(String text) {
        precisionButton.setText(text);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_scale);
        precisionButton.startAnimation(animation);
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
