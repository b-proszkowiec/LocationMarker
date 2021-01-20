package com.example.locationmarker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentListSingleItemAdapter extends RecyclerView.Adapter<FragmentListSingleItemAdapter.FragmentListSingleItemViewHolder> {

    private ArrayList<FragmentListSingleItem> fragmentListSingleItemList;

    public static class FragmentListSingleItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView1;
        public TextView textView2;

        public FragmentListSingleItemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewFragmentList);
            textView1 = itemView.findViewById(R.id.textFragmentListView1);
            textView2 = itemView.findViewById(R.id.textFragmentListView2);
        }
    }

    public FragmentListSingleItemAdapter(ArrayList<FragmentListSingleItem> fragmentListSingleItemArrayList) {
        fragmentListSingleItemList = fragmentListSingleItemArrayList;
    }

    @Override
    public FragmentListSingleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_single_item, parent, false);
        FragmentListSingleItemViewHolder evh = new FragmentListSingleItemViewHolder(view);
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
