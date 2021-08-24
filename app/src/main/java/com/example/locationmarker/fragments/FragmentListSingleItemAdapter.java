package com.example.locationmarker.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.locationmarker.R;

import java.util.ArrayList;

public class FragmentListSingleItemAdapter extends RecyclerView.Adapter<FragmentListSingleItemAdapter.FragmentListSingleItemViewHolder> {

    private ArrayList<FragmentListSingleItem> fragmentListSingleItemList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);

        void onEditClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public static class FragmentListSingleItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView1;
        public TextView textView2;
        public ImageView deleteImage;
        public ImageView editImage;

        public FragmentListSingleItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewFragmentList);
            textView1 = itemView.findViewById(R.id.textFragmentListView1);
            textView2 = itemView.findViewById(R.id.textFragmentListView2);
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

    public FragmentListSingleItemAdapter(ArrayList<FragmentListSingleItem> fragmentListSingleItemArrayList) {
        fragmentListSingleItemList = fragmentListSingleItemArrayList;
    }

    @Override
    public FragmentListSingleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_single_item, parent, false);
        FragmentListSingleItemViewHolder evh = new FragmentListSingleItemViewHolder(view, onItemClickListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(FragmentListSingleItemViewHolder holder, int position) {
        FragmentListSingleItem currentItem = fragmentListSingleItemList.get(position);
        holder.imageView.setImageResource(currentItem.getImageResource());
        holder.textView1.setText(currentItem.getText1());
        holder.textView2.setText(currentItem.getText2());
    }

    @Override
    public int getItemCount() {
        return fragmentListSingleItemList.size();
    }
}
