package com.bpr.pecka;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bpr.pecka.dialog.InputDialog;
import com.bpr.pecka.fragments.ItemFragment;
import com.bpr.pecka.fragments.MapFragment;
import com.bpr.pecka.fragments.SettingsFragment;
import com.bpr.pecka.surface.Surface;
import com.bpr.pecka.surface.SurfaceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // vars
    private final MapFragment mapFragment = new MapFragment();
    private final ItemFragment itemFragment = new ItemFragment();
    private final SettingsFragment settingFragment = new SettingsFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment = mapFragment;
    private TextView toolbarTextView;
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        int itemId = item.getItemId();

        if (itemId == R.id.mapFragment) {
            fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit();
            mapFragment.onHiddenChanged(false);
            toolbarTextView.setText(R.string.app_name);
            activeFragment = mapFragment;
            SurfaceManager.getInstance().hideSurfaceButton();
            return true;
        } else if (itemId == R.id.itemFragment) {
            fragmentManager.beginTransaction().hide(activeFragment).show(itemFragment).commit();
            toolbarTextView.setText(R.string.ic_location_text);
            activeFragment = itemFragment;
            return true;
        } else if (itemId == R.id.settingsFragment) {
            fragmentManager.beginTransaction().hide(activeFragment).show(settingFragment).commit();
            toolbarTextView.setText(R.string.ic_settings_text);
            activeFragment = settingFragment;
            return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SurfaceManager.getInstance().addMapMarkerListener(mapFragment);
        toolbarTextView = this.findViewById(R.id.toolbar_textView);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment, "1").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, settingFragment, "3").hide(settingFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, itemFragment, "2").hide(itemFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SurfaceManager.getInstance().setContext(getApplicationContext());
        SurfaceManager.getInstance().restoreSavedSurfaces();
        InputDialog.getInstance().setContext(this);

        itemFragment.setOnLocationItemClickListener(itemPosition -> {
            fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit();
            activeFragment = mapFragment;
            Surface surface = SurfaceManager.getInstance().getSurfaces().get(itemPosition);
            // set last active surface
            SurfaceManager.getInstance().setLastViewedSurface(surface);
            SurfaceManager.getInstance().refreshView(true, surface);
            mapFragment.hideAddLayerAndMoveToSurface(surface);
        });
    }
}
