package com.bpr.pecka.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bpr.pecka.R;

import java.util.ArrayList;

public class FragmentListSingleItemAdapter extends RecyclerView.Adapter<FragmentListSingleItemAdapter.FragmentListSingleItemViewHolder> {

    private final ArrayList<FragmentListSingleItem> fragmentListSingleItemList;
    private OnItemClickListener onItemClickListener;

    public FragmentListSingleItemAdapter(ArrayList<FragmentListSingleItem> fragmentListSingleItemArrayList) {
        fragmentListSingleItemList = fragmentListSingleItemArrayList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public FragmentListSingleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_single_item, parent, false);
        return new FragmentListSingleItemViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(FragmentListSingleItemViewHolder holder, int position) {
        FragmentListSingleItem currentItem = fragmentListSingleItemList.get(position);
        holder.imageView.setImageResource(currentItem.getImageResource());
        holder.surfaceNameTextView.setText(currentItem.getName());
        holder.areaTextView.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return fragmentListSingleItemList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);

        void onEditClick(int position);
    }

    public static class FragmentListSingleItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView surfaceNameTextView;
        public TextView areaTextView;
        public ImageView deleteImage;
        public ImageView editImage;

        public FragmentListSingleItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewFragmentList);
            surfaceNameTextView = itemView.findViewById(R.id.textFragmentListView1);
            areaTextView = itemView.findViewById(R.id.textFragmentListView2);
            deleteImage = itemView.findViewById(R.id.delete_item);
            editImage = itemView.findViewById(R.id.edit_item);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            deleteImage.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
            editImage.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });
        }
    }
}
