package com.example.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Models.Routine;
import com.example.mobile.R;
import com.example.mobile.RoutineDetailActivity;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {
    private List<Routine> routines;
    private Context context;

    public RoutineAdapter(Context context) {
        this.context = context;
        this.routines = new java.util.ArrayList<>();
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routines.get(position);
        holder.routineName.setText(routine.routineName);
        holder.routineDescription.setText(routine.routineDescription);
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoutineDetailActivity.class);
            intent.putExtra("routineID", routine.routineID);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    public void setRoutines(List<Routine> routines) {
        this.routines = routines;
        notifyDataSetChanged();
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView routineName;
        TextView routineDescription;

        RoutineViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_routine);
            routineName = itemView.findViewById(R.id.routine_name);
            routineDescription = itemView.findViewById(R.id.routine_description);
        }
    }
}