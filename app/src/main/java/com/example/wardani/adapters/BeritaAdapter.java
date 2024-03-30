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
import com.example.wardani.activities.DetailBeritaActivity;
import com.example.wardani.models.BeritaModel;

import java.util.List;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.ViewHolder> {

    private Context context;
    private List<BeritaModel> beritaModelList;

    public BeritaAdapter(Context context, List<BeritaModel> beritaModelList) {
        this.context = context;
        this.beritaModelList = beritaModelList;
    }

    @NonNull
    @Override
    public BeritaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.berita,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BeritaAdapter.ViewHolder holder, int position) {

        Glide.with(context).load(beritaModelList.get(position).getImg_url()).into(holder.image);
        holder.judul.setText(beritaModelList.get(position).getJudul());
        holder.waktu.setText(beritaModelList.get(position).getWaktu());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailBeritaActivity.class);
                intent.putExtra("detail", beritaModelList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return beritaModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView judul,waktu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.berita_image);
            judul = itemView.findViewById(R.id.berita_judul);
            waktu = itemView.findViewById(R.id.berita_waktu);


        }
    }
}
