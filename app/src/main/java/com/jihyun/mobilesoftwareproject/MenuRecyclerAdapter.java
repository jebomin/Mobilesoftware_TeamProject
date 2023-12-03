package com.jihyun.mobilesoftwareproject;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.ViewHolder> {
    private ArrayList<Menudata> menudata;

    @NonNull
    @Override
    public MenuRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(menudata.get(position));
    }

    public void setmenulist(ArrayList<Menudata> list){
        this.menudata = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return menudata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clock_in;
        TextView mn_in;
        TextView kcal_in;
        int id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clock_in = itemView.findViewById(R.id.clock_info);
            mn_in = itemView.findViewById(R.id.mnn_info);
            kcal_in = itemView.findViewById(R.id.kcal_info);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Context context = v.getContext();
                    if (position != RecyclerView.NO_POSITION)
                    {
                        Intent intent = new Intent(context, DetailActivity2.class);
                        intent.putExtra("id", menudata.get(position).getid());
                        intent.putExtra("date", menudata.get(position).getdate());
                        ((MainActivity)context).startActivity(intent);
                    }
                }
            });
        }

        void onBind(Menudata item) {
            clock_in.setText(item.gettime());
            mn_in.setText(item.getmn());
            kcal_in.setText((item.getkcal()) + "kcal");
        }
    }
}