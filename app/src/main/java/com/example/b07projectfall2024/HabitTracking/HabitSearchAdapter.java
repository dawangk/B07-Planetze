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
    public List<Habit> itemList;
    public List<Habit> filteredList;
    private OnItemClickListener onItemClickListener;

    public HabitSearchAdapter(List<Habit> itemList, OnItemClickListener onItemClickListener) {
        this.itemList = itemList;
        this.filteredList = new ArrayList<>(itemList);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Habit habit = filteredList.get(position);
        holder.textView.setText(habit.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(habit);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String search) {
        filteredList.clear();
        if (search.isEmpty()) {
            filteredList.addAll(itemList);
        } else {
            for (Habit habit : itemList) {
                if (habit.getName().toLowerCase().contains(search.toLowerCase())) {
                    filteredList.add(habit);
                }
                else if (habit.getKeywordOne().toLowerCase().contains(search.toLowerCase())) {
                    filteredList.add(habit);
                }
                else if (habit.getKeywordTwo().toLowerCase().contains(search.toLowerCase())) {
                    filteredList.add(habit);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Habit habit);
    }
}

