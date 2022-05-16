package com.example.locationmarker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.locationmarker.dialog.InputDialog;
import com.example.locationmarker.fragments.ItemFragment;
import com.example.locationmarker.fragments.MapFragment;
import com.example.locationmarker.fragments.SettingsFragment;
import com.example.locationmarker.surface.Surface;
import com.example.locationmarker.surface.SurfaceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // vars
    private final MapFragment mapFragment = new MapFragment();
    private final ItemFragment itemFragment = new ItemFragment();
    private final SettingsFragment settingFragment = new SettingsFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment = mapFragment;
    private TextView toolbarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarTextView = this.findViewById(R.id.toolbar_textView);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager.beginTransaction().add(R.id.fragment_container, settingFragment, "3").hide(settingFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, itemFragment, "2").hide(itemFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.mapsFragment:
                fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit();
                mapFragment.onHiddenChanged(false);
                toolbarTextView.setText(R.string.app_name);
                activeFragment = mapFragment;
                SurfaceManager.getInstance().hideSurfaceButton();
                return true;

            case R.id.itemFragment:
                fragmentManager.beginTransaction().hide(activeFragment).show(itemFragment).commit();
                toolbarTextView.setText(R.string.ic_location_text);
                activeFragment = itemFragment;
                return true;

            case R.id.settingsFragment:
                fragmentManager.beginTransaction().hide(activeFragment).show(settingFragment).commit();
                toolbarTextView.setText(R.string.ic_settings_text);
                activeFragment = settingFragment;
                return true;
        }
        return false;
    };

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
