package com.example.b07projectfall2024.HabitTracking;

import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

public class HabitSearchAdapter extends RecyclerView.Adapter<HabitSearchAdapter.ViewHolder> {
    private List<String> dataList;
    private List<String> fullList;

    public HabitSearchAdapter() {
        this.dataList = new ArrayList<>();
        this.fullList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateData(List<String> newData) {
        fullList.clear();
        fullList.addAll(newData);
        filter("");
    }

    public void filter(String text) {
        dataList.clear();
        if (text.isEmpty()) {
            dataList.addAll(fullList);
        } else {
            for (String item : fullList) {
                if (item.toLowerCase().contains(text.toLowerCase())) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

