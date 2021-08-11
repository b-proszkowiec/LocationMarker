package com.example.locationmarker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationmarker.dialog.InputDialog;
import com.example.locationmarker.surface.Surface;
import com.example.locationmarker.surface.SurfaceManager;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment implements FragmentListSingleItemAdapter.OnItemClickListener {
    private static final String LOG_TAG = ItemFragment.class.getSimpleName();
    private static final int WRITE_FILE = 1855;
    private static final int OPEN_FILE = 1856;

    private FragmentListSingleItemAdapter adapter;
    private OnLocationItemClickListener onLocationItemClickListener;
    private TextView noItemsTextView;
    private Button importButton;
    private Button exportButton;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == WRITE_FILE && resultCode == Activity.RESULT_OK) {
            SurfaceManager.getInstance().exportToJson(getContext(), intent.getData());
        } else if (requestCode == OPEN_FILE && resultCode == Activity.RESULT_OK) {
            SurfaceManager.getInstance().importFromJson(intent.getData());
            refreshItemsView();
        } else {
            Toast.makeText(getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnLocationItemClickListener {
        void onLocationItemClickListener(int itemPosition);
    }

    public void setOnLocationItemClickListener(OnLocationItemClickListener listener) {
        onLocationItemClickListener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        noItemsTextView = view.findViewById(R.id.noItemsTextView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        importButton = getActivity().findViewById(R.id.importButton);
        importButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "Import button clicked");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/json");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            this.getActivity().setIntent(intent);
            try {
                this.startActivityForResult(intent, OPEN_FILE);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getContext(), "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        exportButton = getActivity().findViewById(R.id.exportButton);
        exportButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "Export button clicked");
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            this.getActivity().setIntent(intent);
            try {
                this.startActivityForResult(intent, WRITE_FILE);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getContext(), "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshItemsView();
        }
    }

    private void refreshItemsView() {
        ArrayList<FragmentListSingleItem> itemList = new ArrayList<>();
        List<Surface> surfaces = SurfaceManager.getInstance().getSurfaces();

        for (Surface surface : surfaces) {
            itemList.add(new FragmentListSingleItem(R.drawable.ic_single_item_graphic, surface.getName(), surface.getArea()));
        }
        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new FragmentListSingleItemAdapter(itemList);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (itemList.size() == 0) {
            noItemsTextView.setVisibility(View.VISIBLE);
        } else {
            noItemsTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d(LOG_TAG, "onItemClick occurred");
        onLocationItemClickListener.onLocationItemClickListener(position);
    }

    @Override
    public void onDeleteClick(int position) {
        Log.d(LOG_TAG, "onDeleteClick occurred");
        List<Surface> surfaces = SurfaceManager.getInstance().getSurfaces();
        surfaces.remove(position);
        refreshItemsView();
        SurfaceManager.getInstance().storeCurrentSurfaces();
    }

    @Override
    public void onEditClick(int position) {
        Log.d(LOG_TAG, "onEditClick occurred");
        InputDialog.getInstance().setOnDialogTextInputListener(new InputDialog.OnDialogTextInputListener() {
            @Override
            public void onDialogTextInput(int itemPosition, String inputText) {
                Surface surface = SurfaceManager.getInstance().getSurfaces().get(itemPosition);
                surface.setName(inputText);
                SurfaceManager.getInstance().storeCurrentSurfaces();
                adapter.notifyItemChanged(itemPosition);
                refreshItemsView();
            }
        });
        InputDialog.startAlertDialog(position);
    }
}
