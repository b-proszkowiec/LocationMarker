package com.bpr.pecka.fragments;

import static com.bpr.pecka.constants.LocationMarkerConstants.LOCATIONS_ITEM_SELECTED;

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

import com.bpr.pecka.R;
import com.bpr.pecka.SurfaceDetailsActivity;
import com.bpr.pecka.dialog.ConfirmationDialog;
import com.bpr.pecka.dialog.InputDialog;
import com.bpr.pecka.storage.SurfaceRepository;
import com.bpr.pecka.surface.Surface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemFragment extends Fragment implements FragmentListSingleItemAdapter.OnItemClickListener {
    private static final String LOG_TAG = ItemFragment.class.getSimpleName();
    private static final int WRITE_FILE = 1855;
    private static final int OPEN_FILE = 1856;

    private FragmentListSingleItemAdapter adapter;
    private TextView noItemsTextView;
    private TextView itemsInfo;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == WRITE_FILE && resultCode == Activity.RESULT_OK) {
            SurfaceRepository.exportToJsonFile(getContext(), intent.getData());
        } else if (requestCode == OPEN_FILE && resultCode == Activity.RESULT_OK) {
            SurfaceRepository.importFromJsonFile(getContext(), intent.getData());
            refreshItemsView();
        } else {
            Toast.makeText(getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        noItemsTextView = view.findViewById(R.id.noItemsTextView);
        itemsInfo = view.findViewById(R.id.itemsCountInfoTextView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Button importButton = requireActivity().findViewById(R.id.importButton);
        importButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "Import button clicked");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/json");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            requireActivity().setIntent(intent);
            try {
                this.startActivityForResult(intent, OPEN_FILE);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getContext(), "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        Button exportButton = requireActivity().findViewById(R.id.exportButton);
        exportButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "Export button clicked");
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            requireActivity().setIntent(intent);
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
        List<Surface> surfaces = SurfaceRepository.getSurfaces();

        for (Surface surface : surfaces) {
            itemList.add(new FragmentListSingleItem(R.drawable.ic_single_item_graphic, surface.getName(), surface.getArea()));
        }
        RecyclerView recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new FragmentListSingleItemAdapter(itemList);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        String surfaceAmountMessage =  requireContext().getString(R.string.items_amount);
        itemsInfo.setText(String.format(Locale.getDefault(), "%s %d", surfaceAmountMessage, surfaces.size()));
        if (noItemsTextView != null) {
            noItemsTextView.setVisibility(itemList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(int itemNumber) {
        Log.d(LOG_TAG, "onItemClick occurred");
        Intent intent = new Intent(getContext(), SurfaceDetailsActivity.class);
        intent.putExtra(LOCATIONS_ITEM_SELECTED, itemNumber);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int itemIndex) {
        Log.d(LOG_TAG, "onDeleteClick occurred");
        String confirmationTitle = requireContext().getString(R.string.surface_delete_confirmation_title);
        String confirmationMessage= requireContext().getString(R.string.surface_delete_confirmation_message);
        List<Surface> surfaces = SurfaceRepository.getSurfaces();
        Surface surface = surfaces.get(itemIndex);

        ConfirmationDialog.show(requireContext(), () -> {
            surfaces.remove(itemIndex);
            SurfaceRepository.updateInAutoStorage();
            refreshItemsView();
        }, String.format("%s\n\n%s", surface.getName(), confirmationMessage), confirmationTitle);
    }

    @Override
    public void onEditClick(int itemIndex) {
        Log.d(LOG_TAG, "onEditClick occurred");
        Surface surface = SurfaceRepository.getSurfaces().get(itemIndex);
        InputDialog.getInstance().setOnDialogTextInputListener((itemPosition, inputText) -> {
            surface.setName(inputText);
            SurfaceRepository.updateInAutoStorage();
            adapter.notifyItemChanged(itemPosition);
            refreshItemsView();
        });
        InputDialog.getInstance().startAlertDialog(itemIndex, surface.getName());
    }
}
