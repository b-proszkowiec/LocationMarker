package com.example.locationmarker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationmarker.surface.Surface;
import com.example.locationmarker.surface.SurfaceManager;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment implements FragmentListSingleItemAdapter.OnItemClickListener {
    private static final String LOG_TAG = ItemFragment.class.getName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshView();
        }
    }

    private void refreshView() {
        ArrayList<FragmentListSingleItem> itemList = new ArrayList<>();
        List<Surface> surfaces = SurfaceManager.getInstance().getSurfaces();

        for (Surface surface : surfaces) {
            itemList.add(new FragmentListSingleItem(R.drawable.ic_single_item_graphic, surface.getName(), surface.getArea()));
        }
        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        FragmentListSingleItemAdapter adapter = new FragmentListSingleItemAdapter(itemList);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        Log.d(LOG_TAG, "onItemClick occurred");
    }

    @Override
    public void onDeleteClick(int position) {
        Log.d(LOG_TAG, "onDeleteClick occurred");
        List<Surface> surfaces = SurfaceManager.getInstance().getSurfaces();
        surfaces.remove(position);
        refreshView();
    }

    @Override
    public void onEditClick(int position) {
        Log.d(LOG_TAG, "onEditClick occurred");
    }
}
