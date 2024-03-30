package com.example.wardani.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wardani.R;
import com.example.wardani.activities.DetailSenimanActivity;
import com.example.wardani.models.SenimanModel;

import java.util.List;

public class SenimanAdapter extends RecyclerView.Adapter<SenimanAdapter.ViewHolder> {

    private Context context;
    private List<SenimanModel> list;

    public SenimanAdapter(Context context, List<SenimanModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SenimanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.seniman,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.newImg);
        holder.newNama.setText(list.get(position).getNama_dalang());
        holder.newHarga.setText(String.valueOf(list.get(position).getHarga_jasa()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailSenimanActivity.class);
                intent.putExtra("detail", list.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newImg;
        TextView newNama, newHarga;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newImg = itemView.findViewById(R.id.new_img);
            newNama = itemView.findViewById(R.id.nama_seniman);
            newHarga = itemView.findViewById(R.id.harga);
        }
    }
}
