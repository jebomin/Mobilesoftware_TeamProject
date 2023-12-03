package com.jihyun.mobilesoftwareproject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class DateDecision extends RecyclerView.Adapter<DateDecision.DateHolder>{

    ArrayList<String> dayarray;
    Clickevent Click_date;
    private int old = -1;
    private int selected = -1;
    public DateDecision(ArrayList<String> dayarray, Clickevent Click_date){
        this.dayarray = dayarray;
        this.Click_date = Click_date;
    }
    @NonNull
    @Override
    public DateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_date,parent,false);
        return new DateHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateHolder holder, @SuppressLint("RecyclerView") int position) {
        String day2 = dayarray.get(position);
        holder.day.setText(day2);

        if(day2 != null && selected == position)
        {
            holder.day.setBackgroundResource(R.drawable.circle);
        }
        else
        {
            holder.day.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                old = selected;
                selected = position;
                notifyItemChanged(old);
                notifyItemChanged(selected);
                Click_date.Click_date(day2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayarray.size();
    }

    class DateHolder extends RecyclerView.ViewHolder{
        TextView day;
        public DateHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
        }
    }
}
