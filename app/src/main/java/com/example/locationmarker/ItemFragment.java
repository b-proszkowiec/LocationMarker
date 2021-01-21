package com.example.locationmarker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationmarker.surface.Surface;
import com.example.locationmarker.surface.SurfaceManager;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ArrayList<FragmentListSingleItem> itemList = new ArrayList<>();
        List<Surface> surfaces = SurfaceManager.getInstance().getSurfaces();

        for (Surface surface : surfaces) {
            itemList.add(new FragmentListSingleItem(R.drawable.ic_single_item_graphic, surface.getName(), surface.getArea()));
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new FragmentListSingleItemAdapter(itemList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

}
